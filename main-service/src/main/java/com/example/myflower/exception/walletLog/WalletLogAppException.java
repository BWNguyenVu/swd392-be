package com.example.myflower.exception.walletLog;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WalletLogAppException extends RuntimeException{
    private final ErrorCode errorCode;
}
