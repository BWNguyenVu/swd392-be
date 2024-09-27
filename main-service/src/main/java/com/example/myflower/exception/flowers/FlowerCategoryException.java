package com.example.myflower.exception.flowers;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FlowerCategoryException extends RuntimeException {
    private final ErrorCode errorCode;
}
