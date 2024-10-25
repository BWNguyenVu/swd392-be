package com.example.myflower.service;

import com.example.myflower.entity.MediaFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileMediaService {
    @Transactional
    List<MediaFile> uploadMultipleFile(List<MultipartFile> multipartFileList);

    void deleteMultipleFiles(List<MediaFile> mediaFileList);
}
