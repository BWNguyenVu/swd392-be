package com.example.myflower.exception.transaction;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TransactionAppException extends RuntimeException{
    private final ErrorCode errorCode;
}
