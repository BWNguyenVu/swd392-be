package com.example.myflower.service.impl;

import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.dto.payment.requests.CreatePaymentRequestDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.payment.responses.CreatePaymentResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.WalletLog;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.service.AccountService;
import com.example.myflower.service.PaymentService;
import com.example.myflower.service.IWalletLogService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.payos.type.ItemData;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {
    private final PaymentService paymentService;
    private final IWalletLogService iWalletLogService;
    private static final BigDecimal MIN_BALANCE_AMOUNT = new BigDecimal("20000.00");

    // Constructor Injection cho các dependency
    public AccountServiceImpl(PaymentService paymentService, IWalletLogService iWalletLogService) {
        this.paymentService = paymentService;
        this.iWalletLogService = iWalletLogService;
    }


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
            CreatePaymentRequestDTO createPaymentRequestDTO = buildPaymentRequest(addBalanceTitle, amount);

            // Process payment
            CreatePaymentResponseDTO paymentResponse = paymentService.createPayment(createPaymentRequestDTO);

            // Log the wallet transaction
            WalletLog walletLog = logWalletTransaction(account, amount);

            // Return success response
            return ResponseEntity.ok(buildSuccessResponse(paymentResponse, account, walletLog));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    private CreatePaymentRequestDTO buildPaymentRequest(String title, BigDecimal amount) {
        ItemData item = ItemData.builder()
                .name(title)
                .price(amount.intValue())
                .quantity(1)
                .build();

        return CreatePaymentRequestDTO.builder()
                .item(item)
                .totalAmount(amount)
                .note(title)
                .build();
    }

    private WalletLog logWalletTransaction(Account account, BigDecimal amount) {
        WalletLog walletLog = WalletLog.builder()
                .user(account)
                .amount(amount)
                .type(WalletLogTypeEnum.DEPOSIT)
                .status(WalletLogStatusEnum.PENDING)
                .paymentMethod("Payos")
                .build();
        iWalletLogService.createWalletLog(walletLog, account);
        return walletLog;
    }

    private AddBalanceResponseDTO buildSuccessResponse(CreatePaymentResponseDTO paymentResponse, Account account, WalletLog walletLog) {
        return AddBalanceResponseDTO.builder()
                .orderCode(paymentResponse.getOrderCode())
                .addBalance(paymentResponse.getAmount())
                .currentBalance(account.getBalance())
                .currency(paymentResponse.getCurrency())
                .note(paymentResponse.getNote())
                .walletLogTypeEnum(walletLog.getType())
                .checkoutUrl(paymentResponse.getCheckoutUrl())
                .build();
    }

    private AddBalanceResponseDTO createErrorResponse(ErrorCode errorCode) {
        return AddBalanceResponseDTO.builder()
                .message(errorCode.getMessage())
                .error(errorCode.getCode())
                .build();
    }

    public ResponseEntity<GetBalanceResponseDTO> getBalance() {
        Account account = AccountUtils.getCurrentAccount();
        return ResponseEntity.ok(new GetBalanceResponseDTO(
                account.getName(),
                account.getBalance()
        ));
    }

}
