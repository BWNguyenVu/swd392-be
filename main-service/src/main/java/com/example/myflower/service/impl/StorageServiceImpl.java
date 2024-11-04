package com.example.myflower.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.myflower.consts.Constants;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private static final Logger LOG = LogManager.getLogger(StorageServiceImpl.class);

    @Value("${cloud.aws.using.s3}")
    private Boolean isUsingS3;
    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    private final RedisCommandService redisCommandService;
    private final AmazonS3 s3Client;

    @Override
    public String uploadFile(MultipartFile uploadedFile) throws IOException {
        LOG.info("[uploadFile] Start upload file, isUsingS3={}", isUsingS3);
        File file = FileUtils.convertMultiPartFileToFile(uploadedFile);
        try {
            if (Boolean.TRUE.equals(isUsingS3)) {
                String fileName = FileUtils.generateFileName(uploadedFile);
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
                LOG.info("[uploadFile] File uploaded to S3 with filename: {}", fileName);
                return fileName;
            }
        }
        finally {
            file.delete();
        }
        return "";
    }

    @Override
    public void deleteFile(String fileName) {
        LOG.info("[deleteFile] Start delete file with filename={}, isUsingS3={}", fileName, isUsingS3);
        if (Boolean.TRUE.equals(isUsingS3)) {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        LOG.info("[getFileUrl] Start get file url with filename={}, isUsingS3={}", fileName, isUsingS3);
        if (Boolean.TRUE.equals(isUsingS3)) {
            String cacheResult = redisCommandService.getPresignedUrl(fileName);
            if (cacheResult != null) {
                return cacheResult;
            }
            String url = this.generatePresignedUrl(fileName);
            redisCommandService.storePresignedUrl(fileName, url);
            LOG.info("[getFileUrl] Completed with presigned url {}", url);
            return url;
        }
        return fileName;
    }

    @Override
    public void clearPresignedUrlCache() {
        LOG.info("[clearPresignedUrlCache] Start clear aws s3 presigned url");
        redisCommandService.clearPresignedUrlCache();
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
        expTimeMillis += Constants.S3_PRESIGNED_URL_EXPIRATION_MILISECONDS;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
