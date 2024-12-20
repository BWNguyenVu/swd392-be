package com.example.myflower.controller;

import com.example.myflower.dto.BaseResponseDTO;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.requests.*;
import com.example.myflower.dto.auth.responses.*;
import com.example.myflower.service.AuthService;
import com.example.myflower.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("**")
public class AuthController {
    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private AuthService authService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome XD";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authServiceImpl.checkLogin(loginRequestDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerAccount(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return authServiceImpl.registerAccount(registerRequestDTO);
    }

    @PostMapping("/verify/{token}")
    public ResponseEntity<BaseResponseDTO> activateAccount(@PathVariable String token) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDTO.builder()
                        .success(true)
                        .data(authServiceImpl.verifyAccount(token))
                        .message("Verify account successfully")
                        .build()

        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequest) {
        return authServiceImpl.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@RequestParam("token") String token,
                                                                  @RequestBody ResetPasswordRequestDTO resetPasswordRequest) {
        return authServiceImpl.resetPassword(resetPasswordRequest, token);

    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        return authServiceImpl.changePassword(changePasswordRequest);
    }

    @PostMapping("/renew-access-token")
    public ResponseEntity<BaseResponseDTO> renewAccessToken(@RequestHeader("x-refresh-token") String refreshToken) {
        try {
            final String message = "Renew access token successfully";
            AuthResponseDTO renewAccessToken = authServiceImpl.renewAccessToken(refreshToken);
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(renewAccessToken.getAccessToken())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDTO.builder()
                    .message(message)
                    .data(response)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDTO.builder()
                    .message(e.getMessage())
                    .build());
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PostMapping("/change-email")
    public ResponseEntity<BaseResponseDTO> changeEmail(@RequestBody ChangeEmailRequestDTO changeEmailRequest) {
        AccountResponseDTO accountResponseDTO = authService.changeEmail(changeEmailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseDTO.builder()
                        .message("Sending otp to your email successfully")
                        .success(true)
                        .data(accountResponseDTO)
                        .build()
        );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'MANAGER')")
    @PostMapping("/confirm-change-email/{otp}")
    public ResponseEntity<BaseResponseDTO> confirmChangeEmail(@PathVariable String otp) {
        AccountResponseDTO accountResponseDTO = authService.confirmChangeEmail(otp);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseDTO.builder()
                        .message("Change email successfully")
                        .success(true)
                        .data(accountResponseDTO)
                        .build()
        );
    }

    @PostMapping("/introspect")
    public ResponseEntity<BaseResponseDTO> checkIntrospect(@RequestBody IntrospectRequestDTO request) {
        IntrospectResponseDTO responseDTO = authService.introspect(request);
        System.out.println("Introspect hello");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseDTO.builder()
                        .message("Check introspection successfully")
                        .success(true)
                        .data(responseDTO)
                        .build()
        );
    }
}
