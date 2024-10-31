package com.example.myflower.exception.feedback;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FeedbackAppException extends RuntimeException {
    private final ErrorCode errorCode;
}
