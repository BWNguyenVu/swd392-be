package com.example.myflower.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String uploadFile(MultipartFile uploadedFile) throws IOException;
    String getFileUrl(String fileName);
}