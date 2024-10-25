package com.example.myflower.exception.mediaFile;

import com.example.myflower.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MediaFileException extends RuntimeException {
    private final ErrorCode errorCode;
}
