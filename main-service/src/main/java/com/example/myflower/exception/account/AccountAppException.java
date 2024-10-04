package com.example.myflower.exception.account;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AccountAppException extends RuntimeException {
  private final ErrorCode errorCode;
}
