package com.example.myflower.service.impl;

import com.example.myflower.consts.Constants;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.responses.AuthResponseDTO;
import com.example.myflower.dto.auth.requests.*;
import com.example.myflower.dto.auth.responses.*;
import com.example.myflower.dto.notification.NotificationMessageDTO;
import com.example.myflower.dto.jwt.requests.GenerateAccessTokenRequestDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.enumType.*;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.account.AccountAppException;
import com.example.myflower.exception.auth.AuthAppException;
import com.example.myflower.mapper.AccountMapper;
import com.example.myflower.repository.AccountRepository;
import com.example.myflower.service.AuthService;
import com.example.myflower.service.JWTService;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.AccountUtils;
import com.nimbusds.jose.JOSEException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthServiceImpl implements UserDetailsService, AuthService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RedisCommandService redisCommandService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, Account> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, NotificationMessageDTO> kafkaNotificationTemplate;

    @Autowired
    private StorageService storageService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userDetails;
    }
    public Account getAccountByEmail(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        return account.orElse(null);
    }

    public ResponseEntity<LoginResponseDTO> checkLogin(LoginRequestDTO loginRequestDTO) {
        try {
            // GET EMAIL BY REQUEST DTO AND VALIDATION EMAIL
            Account account = getAccountByEmail(loginRequestDTO.getEmail());
            if (account == null) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                throw new AuthAppException(ErrorCode.ACCOUNT_NOT_VERIFY);
            }
            if (account.getStatus().equals(AccountStatusEnum.BAN)) {
                throw new AuthAppException(ErrorCode.ACCOUNT_BANNED);
            }
            Authentication authentication = null;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDTO.getEmail(),
                                loginRequestDTO.getPassword()
                        )
                );
            } catch (Exception e) {
                throw new AuthAppException(ErrorCode.USERNAME_PASSWORD_NOT_CORRECT);
            }


            Account returnAccount = (Account) authentication.getPrincipal();
            // CALL FUNC || GENERATE TOKEN (1DAY) AND REFRESH TOKEN (7DAYS)
            String refreshToken = redisCommandService.getValidRefreshTokenByUserId(account.getId());
            if (refreshToken == null) {
                refreshToken = jwtService.generateRefreshToken(account.getEmail());
                redisCommandService.storeRefreshToken(account.getId(), refreshToken);
            }
            account.setTokens(jwtService.generateAccessToken(
                    GenerateAccessTokenRequestDTO.builder()
                            .userId(account.getId())
                            .role(account.getRole())
                            .email(account.getEmail())
                            .build()
            ));
            account.setRefreshToken(refreshToken);

            String responseString = "Login successful";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    responseString,
                    null,
                    returnAccount.getTokens(),
                    returnAccount.getRefreshToken()
            );
            return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);


        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorResponse = "Login failed";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    e.getMessage(),
                    errorResponse,
                    null,
                    null
            );
            return new ResponseEntity<>(loginResponseDTO, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<RegisterResponseDTO> registerAccount(RegisterRequestDTO registerRequestDTO) {
        try {
            Account tempAccount = getAccountByEmail(registerRequestDTO.getEmail());
            if (tempAccount != null) {
                if (tempAccount.getStatus().equals(AccountStatusEnum.VERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_EXISTED);
                } else if (tempAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_WAIT_VERIFY);
                }
            }
            Account account = getAccount(registerRequestDTO);
            accountRepository.save(account);
            // GENERATE TOKEN FOR CONFIRM EMAIL REGISTER (ENSURE THAT UNIQUE AND JUST ONLY THIS EMAIL CAN USE)
            String token = jwtService.generateToken(account.getEmail());
            account.setTokens(token);

            kafkaTemplate.send("email_register_account_topic", account);
            /* Send email right here if kafka is broken
            Email Service send Verify Account Mail Template */
            String responseMessage = "Successful registration, please check your email for verification";
            RegisterResponseDTO response = new RegisterResponseDTO(responseMessage, null, 201, registerRequestDTO.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorMessage = "Register failed";
            RegisterResponseDTO response = new RegisterResponseDTO(errorMessage, errorCode.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(response, errorCode.getHttpStatus());
        }
    }

    private @NotNull Account getAccount(RegisterRequestDTO registerRequestDTO) {
        Account account = new Account();
        account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        account.setExternalAuthType(AccountProviderEnum.LOCAL);
        account.setGender(registerRequestDTO.getAccountGenderEnum());
        account.setRole(AccountRoleEnum.USER);
        account.setPhone(registerRequestDTO.getPhone());
        account.setAvatar(Constants.DEFAULT_USER_AVATAR);
        account.setName(registerRequestDTO.getName());
        account.setEmail(registerRequestDTO.getEmail());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatusEnum.UNVERIFIED);
        account.setCreateAt(LocalDateTime.now());
        return account;
    }

    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequest) {
        try {
            // CHECK VALID EMAIL
            Optional<Account> tempAccount = accountRepository.findByEmail(forgotPasswordRequest.getEmail());

            Account checkAccount = tempAccount.orElseThrow(() -> new AuthAppException(ErrorCode.EMAIL_NOT_FOUND));

            if (checkAccount.getEmail() == null || checkAccount.getEmail().isEmpty() || checkAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            // GENERATE TOKEN FOR EMAIL FORGOT PASSWORD (ENSURE UNIQUE AND JUST ONLY EMAIL CAN USE)
            String token = jwtService.generateToken(forgotPasswordRequest.getEmail());
            Account account = tempAccount.orElseThrow(() -> new UsernameNotFoundException("User not found"));
            account.setTokens(token);
            // KAFKA SEND MESSAGE TO NOTIFICATION SERVICE
            kafkaTemplate.send("email_forgot_password_topic", account);

            accountRepository.save(account);
            ForgotPasswordResponseDTO forgotPasswordResponse = new ForgotPasswordResponseDTO("Password reset token generated successfully.", null, 200);
            return new ResponseEntity<>(forgotPasswordResponse, HttpStatus.OK);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ForgotPasswordResponseDTO forgotPasswordResponse = new ForgotPasswordResponseDTO("Password reset failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(forgotPasswordResponse, errorCode.getHttpStatus());
        }
    }

    private String getEmailByToken(String token) {
        String email = jwtService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            throw new AuthAppException(ErrorCode.TOKEN_INVALID);
        }
        return email;
    }

    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(ResetPasswordRequestDTO resetPasswordRequest, String token) {
        try {
            // AFTER USER CLICK LINK FORGOT PASSWORD IN EMAIL THEN REDIRECT TO API HERE (RESET PASSWORD)
            // CHECK PASSWORD AND REPEAT PASSWORD
            if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getRepeatPassword())) {
                throw new AuthAppException(ErrorCode.PASSWORD_REPEAT_INCORRECT);
            }
            // CALL FUNC
            String email = getEmailByToken(token);
            // FIND EMAIL IN DATABASE AND UPDATE NEW PASSWORD
            Optional<Account> accountOptional = accountRepository.findByEmail(email);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
                accountRepository.save(account);

                //Revoked all refresh tokens associated with this account
                redisCommandService.revokeRefreshToken(account.getId());
            }

            ResetPasswordResponseDTO resetPasswordResponse = new ResetPasswordResponseDTO("Password reset token generated successfully.", null, 200);
            return new ResponseEntity<>(resetPasswordResponse, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ResetPasswordResponseDTO resetPasswordResponse = new ResetPasswordResponseDTO("Password reset failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(resetPasswordResponse, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<ChangePasswordResponseDTO> changePassword(ChangePasswordRequestDTO changePasswordRequest) {
        try {
            // GET ACCOUNT FROM TOKEN IN HEADER || FUNCTION REQUIRED: ENSURE USER'S LOGIN AND REMEMBER OLD PASSWORD
            Account account = AccountUtils.getCurrentAccount();
            // CHECK OLD PASSWORD
            if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), account.getPassword())) {
                throw new AuthAppException(ErrorCode.OLD_PASSWORD_INCORRECT);
            }
            // CHECK NEW PASSWORD MATCH REPEAT PASSWORD
            if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getRepeatPassword())) {
                throw new AuthAppException(ErrorCode.PASSWORD_REPEAT_INCORRECT);
            }
            // ENCODED PASSWORD AFTER SAVE TO DATABASE
            account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            accountRepository.save(account);

            ChangePasswordResponseDTO changePasswordResponse = new ChangePasswordResponseDTO("Password changed successfully", null, 200);
            return new ResponseEntity<>(changePasswordResponse, HttpStatus.OK);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ChangePasswordResponseDTO changePasswordResponse = new ChangePasswordResponseDTO("Password change failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(changePasswordResponse, errorCode.getHttpStatus());
        }
    }

    public boolean verifyAccount(String token) throws Exception {
        try {
            String email = jwtService.extractEmail(token);

            Account accountEntity = getAccountByEmail(email);
            accountEntity.setStatus(AccountStatusEnum.VERIFIED);
            accountRepository.save(accountEntity);
            NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                    .userId(accountEntity.getId())
                    .title("Welcome")
                    .message("Welcome " + accountEntity.getName() + " to our platform!")
                    .destinationScreen(DestinationScreenEnum.HOME_PAGE)
                    .type(NotificationTypeEnum.WELCOME)
                    .build();
            // KAFKA SEND MESSAGE TO NOTIFICATION SERVICE
            kafkaNotificationTemplate.send("push_notification_topic", notificationMessageDTO);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token!", e);
        }
    }

    @Override
    public AuthResponseDTO renewAccessToken(String token) {
        String email = jwtService.extractEmail(token);
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AccountAppException(ErrorCode.ACCOUNT_NOT_FOUND));
        String accessToken = jwtService.generateToken(account.getEmail());
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public AccountResponseDTO changeEmail(ChangeEmailRequestDTO changeEmailRequestDTO){
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if(account.getEmail().equals(changeEmailRequestDTO.getEmail())){
            throw new AuthAppException(ErrorCode.EMAIL_EXISTED);
        }
        Optional<Account> accountOptional = accountRepository.findByEmail(changeEmailRequestDTO.getEmail());
        if (accountOptional.isPresent()) {
            throw new AuthAppException(ErrorCode.EMAIL_DUPLICATE);
        }
        account.setEmail(changeEmailRequestDTO.getEmail());
        account.setOtp(generateRandomOtp());
        String encodeOtp = jwtService.generateTokenByOtp(account.getOtp()) ;
        redisCommandService.storeOtpChangeEmail(account.getId(), encodeOtp, changeEmailRequestDTO.getEmail());
        redisCommandService.storeOtpChangeEmail(account.getId(), changeEmailRequestDTO.getEmail(), "change-email");

        kafkaTemplate.send("email_change-topic", account);
        return accountMapper.mapToAccountResponseDTO(account);
    }

    private String generateRandomOtp() {
        int otpLength = 6;
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Override
    public AccountResponseDTO confirmChangeEmail(String otp) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        // Fetch OTP from Redis
        String changeEmail = redisCommandService.getOtpChangeEmail(account.getId(),"change-email");
        String storedOtp = redisCommandService.getOtpChangeEmail(account.getId(), changeEmail);

        if (storedOtp == null) {
            throw new AuthAppException(ErrorCode.OTP_NOT_FOUND);
        }
        String encodeOtp = jwtService.extractEmail(storedOtp);

        if (!encodeOtp.equals(otp)) {
            throw new AuthAppException(ErrorCode.INVALID_OTP);
        }
        redisCommandService.deleteOtp(account.getId(), changeEmail, "change-email");
        account.setEmail(changeEmail);
        account.setUpdateAt(LocalDateTime.now());
        accountRepository.save(account);
    return accountMapper.mapToAccountResponseDTO(account);

    }
    @Override
    public IntrospectResponseDTO introspect(IntrospectRequestDTO request)  {
        return IntrospectResponseDTO.builder()
                .valid(jwtService.verifyToken(request.getToken(), false))
                .build();
    }
}
