package com.example.myflower.exception.order;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrderAppException extends RuntimeException{
    private final ErrorCode errorCode;
}
