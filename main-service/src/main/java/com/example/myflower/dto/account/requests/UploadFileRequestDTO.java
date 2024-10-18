package com.example.myflower.dto.account.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class UploadFileRequestDTO {
    private MultipartFile file;
}
