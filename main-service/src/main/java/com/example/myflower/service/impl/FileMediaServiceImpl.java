package com.example.myflower.service.impl;

import com.example.myflower.entity.MediaFile;
import com.example.myflower.entity.enumType.StorageMethodEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.mediaFile.MediaFileException;
import com.example.myflower.repository.MediaFileRepository;
import com.example.myflower.service.FileMediaService;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.FileUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileMediaServiceImpl implements FileMediaService {
    @Value("${cloud.aws.using.s3}")
    private Boolean isUsingS3;

    @NonNull
    private StorageService storageService;
    @NonNull
    private MediaFileRepository mediaFileRepository;

    @Override
    @Transactional
    public List<MediaFile> uploadMultipleFile(List<MultipartFile> multipartFileList) {
        try {
            List<String> fileNameList = new ArrayList<>();
            for (MultipartFile file : multipartFileList) {
                String fileName = storageService.uploadFile(file);
                fileNameList.add(fileName);
            }
            List<MediaFile> mediaFileList = new ArrayList<>();
            for (String fileName : fileNameList) {
                MediaFile mediaFile = MediaFile.builder()
                        .fileName(fileName)
                        .storageMethod(Boolean.TRUE.equals(isUsingS3) ? StorageMethodEnum.S3 : StorageMethodEnum.LOCAL)
                        .createdAt(LocalDateTime.now())
                        .isDeleted(Boolean.FALSE)
                        .build();
                mediaFileList.add(mediaFile);
            }
            return mediaFileRepository.saveAll(mediaFileList);
        }
        catch (Exception e) {
            throw new MediaFileException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
