package com.example.myflower.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    @Value("${cloud.aws.using.s3}")
    private Boolean isUsingS3;
    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Override
    public String uploadFile(MultipartFile uploadedFile) throws IOException {
        if (Boolean.TRUE.equals(isUsingS3)) {
            File file = FileUtils.convertMultiPartFileToFile(uploadedFile);
            String fileName = System.currentTimeMillis() + "_" + uploadedFile.getOriginalFilename();
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
            return fileName;
        }
        return "";
    }

    @Override
    public String getFileUrl(String fileName) {
        if (Boolean.TRUE.equals(isUsingS3)) {
            return this.generatePresignedUrl(fileName);
        }
        return fileName;
    }

    private String generatePresignedUrl(String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
        generatePresignedUrlRequest.withMethod(HttpMethod.GET);
        generatePresignedUrlRequest.withExpiration(this.generatePresignedUrlExpiration());
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    private Date generatePresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
