package com.example.myflower.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String uploadFile(MultipartFile uploadedFile) throws IOException;

    void deleteFile(String fileName);

    String getFileUrl(String fileName);
}