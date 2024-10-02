package com.example.myflower.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //    ACCOUNTS | CODE: 1XXX
    //    Accounts | Auth
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "User name must be at least 3 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1006, "Invalid token", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_VERIFY(1007, "This account has not been verified", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(1008, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    OLD_PASSWORD_INCORRECT(1009, "Old password incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_REPEAT_INCORRECT(1010, "Password repeat do not match", HttpStatus.BAD_REQUEST),
    NOT_LOGIN(1011, "You need to login", HttpStatus.BAD_REQUEST),
    USERNAME_PASSWORD_NOT_CORRECT(1012, "Username or password is not correct", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(1013,"Account not found", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_INSTRUCTOR(1014,"Account not instructor", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_STUDENT(1015,"Account not student", HttpStatus.BAD_REQUEST),
    ADD_BALANCE_INVALID(1016, "Add balance amount must be greater than 20.000 VNĐ", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1017,"Email not found, please register account.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1018, "Unauthorized", HttpStatus.UNAUTHORIZED),

    //    Accounts | Emails | CODE: 15XX
    EMAIL_WAIT_VERIFY(1501, "This email has been registered and is not verified, please verify and login", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1502, "This email has been registered, please log in!", HttpStatus.BAD_REQUEST),
    SUCCESS(200, "Success",HttpStatus.OK),

    //    ORDERS | CODE: 5XXX
    ORDER_NOT_FOUND(5000, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_INVALID_FUNDS(5001, "Insufficient funds: Please top up your balance to complete this transaction", HttpStatus.BAD_REQUEST),

    //    WALLETS | CODE: 6XXX
    PRICE_INVALID(6000, "Price must be greater than 0", HttpStatus.BAD_REQUEST),
    BALANCE_INVALID(6001, "Amount must be greater than 0 and less than the remaining balance.", HttpStatus.BAD_REQUEST),
    BALANCE_NOT_ENOUGH(6002, "Doesn't have enough balance", HttpStatus.BAD_REQUEST),

    //    FLOWERS | CODE: 3XXX
    FLOWER_NOT_FOUND(3000, "Flower not found", HttpStatus.NOT_FOUND),
    FLOWER_CATEGORY_NOT_FOUND(3101, "Flower category not found", HttpStatus.NOT_FOUND),
    INVALID_IMAGE(3102, "Invalid image file", HttpStatus.BAD_REQUEST)
    ;
    @Getter
    private final Integer code;
    @Setter
    private String message;
    @Getter
    private final HttpStatus httpStatus;

    ErrorCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
