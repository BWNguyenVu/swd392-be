package com.example.myflower.service.impl;

import com.example.myflower.consts.Constants;
import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.requests.GetUsersRequestDTO;
import com.example.myflower.dto.account.requests.UpdateAccountRequestDTO;
import com.example.myflower.dto.account.requests.UploadFileRequestDTO;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.dto.account.responses.SellerResponseDTO;
import com.example.myflower.dto.feedback.response.RatingFeedbackResponseDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.dto.payment.requests.CreatePaymentResponseDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.payment.responses.PaymentResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.entity.enumType.*;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.account.AccountAppException;
import com.example.myflower.exception.auth.AuthAppException;
import com.example.myflower.mapper.AccountMapper;
import com.example.myflower.repository.AccountRepository;
import com.example.myflower.service.*;
import com.example.myflower.utils.AccountUtils;
import com.example.myflower.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.payos.type.ItemData;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final PaymentService paymentService;
    private final WalletLogService walletLogService;
    private static final BigDecimal MIN_BALANCE_AMOUNT = new BigDecimal("20000.00");
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FeedbackService feedbackService;
    // Constructor Injection cho các dependency
    public AccountServiceImpl(PaymentService paymentService, WalletLogService walletLogService) {
        this.paymentService = paymentService;
        this.walletLogService = walletLogService;
    }

    @Autowired
    private StorageService storageService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    @Lazy
    private FlowerListingService flowerListingService;

    @Override
    public ResponseEntity<AddBalanceResponseDTO> addBalance(AddBalanceRequestDTO addBalanceRequestDTO) {
        final String addBalanceTitle = "Add balance";
        BigDecimal amount = addBalanceRequestDTO.getAmount();

        // Validate balance amount
        if (amount.compareTo(MIN_BALANCE_AMOUNT) < 0) {
            return ResponseEntity.badRequest().body(createErrorResponse(ErrorCode.ADD_BALANCE_INVALID));
        }

        Account account = AccountUtils.getCurrentAccount();

        try {
            // Create payment request
            CreatePaymentResponseDTO createPaymentResponseDTO = buildPaymentRequest(addBalanceTitle, amount);

            // Process payment
            PaymentResponseDTO paymentResponse = paymentService.createPayment(createPaymentResponseDTO, account);

            Payment payment = Payment.builder()
                    .id(paymentResponse.getId())
                    .user(account)
                    .paymentLinkId(paymentResponse.getPaymentLinkId())
                    .amount(paymentResponse.getAmount())
                    .currency(paymentResponse.getCurrency())
                    .checkoutUrl(paymentResponse.getCheckoutUrl())
                    .createdAt(paymentResponse.getCreateAt())
                    .build();

            // Log the wallet transaction
            WalletLog walletLog = logWalletTransaction(account, amount, payment);

            // Return success response
            return ResponseEntity.ok(buildSuccessResponse(paymentResponse, account, walletLog));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    private CreatePaymentResponseDTO buildPaymentRequest(String title, BigDecimal amount) {
        ItemData item = ItemData.builder()
                .name(title)
                .price(amount.intValue())
                .quantity(1)
                .build();

        return CreatePaymentResponseDTO.builder()
                .item(item)
                .totalAmount(amount)
                .note(title)
                .build();
    }

    private WalletLog logWalletTransaction(Account account, BigDecimal amount, Payment payment) {
        WalletLog walletLog = WalletLog.builder()
                .actorEnum(WalletLogActorEnum.DEPOSITOR)
                .user(account)
                .amount(amount)
                .type(WalletLogTypeEnum.DEPOSIT)
                .status(WalletLogStatusEnum.PENDING)
                .paymentMethod(PaymentMethodEnum.PAYOS)
                .payment(payment)
                .build();
        walletLogService.createWalletLog(walletLog, account);
        return walletLog;
    }

    private AddBalanceResponseDTO buildSuccessResponse(PaymentResponseDTO paymentResponse, Account account, WalletLog walletLog) {
        return AddBalanceResponseDTO.builder()
                .id(paymentResponse.getId())
                .orderCode(paymentResponse.getOrderCode())
                .addBalance(paymentResponse.getAmount())
                .currentBalance(account.getBalance())
                .currency(paymentResponse.getCurrency())
                .note(paymentResponse.getNote())
                .walletLogTypeEnum(walletLog.getType())
                .checkoutUrl(paymentResponse.getCheckoutUrl())
                .paymentLinkId(paymentResponse.getPaymentLinkId())
                .createAt(paymentResponse.getCreateAt())
                .build();
    }

    private AddBalanceResponseDTO createErrorResponse(ErrorCode errorCode) {
        return AddBalanceResponseDTO.builder()
                .message(errorCode.getMessage())
                .error(errorCode.getCode())
                .build();
    }

    @Override
    public ResponseEntity<GetBalanceResponseDTO> getBalance() {
        Account account = AccountUtils.getCurrentAccount();
        return ResponseEntity.ok(new GetBalanceResponseDTO(
                account.getName(),
                account.getBalance()
        ));
    }

    @Override
    @Transactional
    public Account handleBalanceByOrder(Account account, BigDecimal amount, WalletLogTypeEnum type, WalletLogActorEnum actorEnum, OrderSummary orderSummary, Payment payment, WalletLogStatusEnum status, Boolean isRefund) {
        BigDecimal balance = adjustAccountBalance(account, amount, type);
        switch (type) {
            case ADD:
                WalletLog walletLogAdd = createWalletLog(account, amount, type, actorEnum, payment, status, isRefund);
                if(isRefund) {
                    if (actorEnum == WalletLogActorEnum.BUYER) {
                        createTransaction(account, orderSummary, walletLogAdd);
                    }
                }
                break;
            case SUBTRACT:
                WalletLog walletLogSubtract = createWalletLog(account, amount, type, actorEnum, payment, status, isRefund);
                if (actorEnum == WalletLogActorEnum.BUYER) {
                    createTransaction(account, orderSummary, walletLogSubtract);
                }
                if(isRefund) {
                    if (actorEnum == WalletLogActorEnum.ADMIN) {
                        createTransaction(account, orderSummary, walletLogSubtract);
                    }
                    if (actorEnum == WalletLogActorEnum.SELLER) {
                        createTransaction(account, orderSummary, walletLogSubtract);
                    }
                }
                break;
            case DEPOSIT:
                walletLogService.updateWalletLogByPayment(payment, status, balance);
                break;
            default:
                throw new IllegalArgumentException("Unsupported WalletLogTypeEnum: " + type);
        }
        account.setUpdateAt(LocalDateTime.now());
        return saveAccount(account);
    }

    private BigDecimal adjustAccountBalance(Account account, BigDecimal amount, WalletLogTypeEnum type) {
        BigDecimal balance = account.getBalance();
        if (type == WalletLogTypeEnum.ADD || type == WalletLogTypeEnum.DEPOSIT) {
            account.setBalance(account.getBalance().add(amount));
            balance = account.getBalance();
        } else if (type == WalletLogTypeEnum.SUBTRACT) {
            account.setBalance(account.getBalance().subtract(amount));
            balance = account.getBalance();
        }
        return balance;
    }

    private Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    private WalletLog createWalletLog(Account account, BigDecimal amount, WalletLogTypeEnum type, WalletLogActorEnum actorEnum, Payment payment, WalletLogStatusEnum status, Boolean isRefund) {
        WalletLog walletLog = WalletLog.builder()
                .balance(account.getBalance())
                .user(account)
                .amount(amount)
                .type(type)
                .paymentMethod(PaymentMethodEnum.WALLET)
                .status(status)
                .actorEnum(actorEnum)
                .payment(payment)
                .createdAt(LocalDateTime.now())
                .isRefund(isRefund)
                .build();
        walletLogService.createWalletLog(walletLog, account);
        return walletLog;
    }

    private void createTransaction(Account account, OrderSummary orderSummary, WalletLog walletLog) {
        Transaction transaction = Transaction.builder()
                .user(account)
                .walletLog(walletLog)
                .orderSummary(orderSummary)
                .createdAt(LocalDateTime.now())
                .build();

        transactionService.createTransaction(transaction, account);
    }

    @Override
    public AccountResponseDTO getProfile() {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new AccountAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return accountMapper.mapToAccountResponseDTO(account);
    }

    @Override
    public SellerResponseDTO getSellerById(Integer sellerId){
        Optional<Account> account = accountRepository.findById(sellerId);
        Integer countProduct = flowerListingService.countProductBySeller(sellerId);
        RatingFeedbackResponseDTO feedbackResponseDTO = feedbackService.ratingByUserId(sellerId);
        account.get().setAvatar(storageService.getFileUrl(account.get().getAvatar()));
        return SellerResponseDTO.builder()
                .id(account.get().getId())
                .name(account.get().getName())
                .email(account.get().getEmail())
                .phone(account.get().getPhone())
                .avatar(account.get().getAvatar())
                .gender(account.get().getGender())
                .ratingAverage(feedbackResponseDTO.getAverage())
                .ratingCount(feedbackResponseDTO.getCountFeedback())
                .productCount(countProduct)
                .createAt(account.get().getCreateAt())
                .build();
    }


    @Override
    public AccountResponseDTO uploadAvatar(UploadFileRequestDTO uploadFileRequestDTO) throws IOException {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        MultipartFile imageFile = uploadFileRequestDTO.getFile();
        if (!ValidationUtils.validateImage(imageFile)) {
            throw new AccountAppException(ErrorCode.INVALID_IMAGE);
        }
        String imageUrl = storageService.uploadFile(uploadFileRequestDTO.getFile());
        account.setAvatar(imageUrl);
        accountRepository.save(account);
        return accountMapper.mapToAccountResponseDTO(account);
    }

    @Override
    public AccountResponseDTO updateProfile(UpdateAccountRequestDTO updateAccountRequestDTO){
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        account.setName(updateAccountRequestDTO.getName());
        account.setGender(updateAccountRequestDTO.getGender());
        account.setPhone(updateAccountRequestDTO.getPhone());
        account.setUpdateAt(LocalDateTime.now());
        accountRepository.save(account);
        return accountMapper.mapToAccountResponseDTO(account);
    }

    @Override
    public AccountResponseDTO updateStatusUser(UpdateAccountRequestDTO updateAccountRequestDTO, Integer userId){
        Account account = AccountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND));

        user.setStatus(updateAccountRequestDTO.getStatus());
        accountRepository.save(user);
        return accountMapper.mapToAccountResponseDTO(user);
    }


    @Override
    public PaginationResponseDTO<AccountResponseDTO> getAllUser(GetUsersRequestDTO requestDTO) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || !account.getRole().equals(AccountRoleEnum.ADMIN)) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if (requestDTO.getRoles() != null && requestDTO.getRoles().isEmpty()) {
            requestDTO.setRoles(null);
        }
        //Construct sort by field parameters
        Sort sort;
        switch (requestDTO.getSortBy()) {
            case "name":
                sort = Sort.by("name");
                break;
            case "email":
                sort = Sort.by("email");
                break;
            default:
                sort = Sort.by("createAt");
                break;
        }
        if (Constants.SORT_ORDER_DESCENDING.equals(requestDTO.getOrder())) {
            sort = sort.descending();
        }
        //Construct pagination and sort parameters
        Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize(), sort);
        Page<Account> accountList = accountRepository
                .findAccountWithParameters(requestDTO.getRoles(), requestDTO.getSearch(), pageable);
        return accountMapper.toPaginationResponseDTO(accountList);
    }

    @Override
    public AccountResponseDTO getProfileById(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.mapToAccountResponseDTO(account);
    }
}
