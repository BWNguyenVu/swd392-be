package com.example.myflower.service.impl;

import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.entity.MediaFile;
import com.example.myflower.entity.enumType.StorageMethodEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.mediaFile.MediaFileException;
import com.example.myflower.mapper.MediaFileMapper;
import com.example.myflower.repository.MediaFileRepository;
import com.example.myflower.service.FileMediaService;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.StorageService;
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
    private RedisCommandService redisCommandService;
    @NonNull
    private MediaFileRepository mediaFileRepository;
    @NonNull
    private MediaFileMapper mediaFileMapper;

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
            //Save to database
            List<MediaFile> result = mediaFileRepository.saveAll(mediaFileList);
            //Add to cache
            result.stream()
                    .map(mediaFileMapper::toResponseDTO)
                    .forEach(fileDTO -> redisCommandService.storeMediaFile(fileDTO));
            return result;
        }
        catch (Exception e) {
            throw new MediaFileException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteMultipleFiles(List<MediaFile> mediaFileList) {
        try {
            for (MediaFile mediaFile : mediaFileList) {
                storageService.deleteFile(mediaFile.getFileName());
            }
            mediaFileRepository.deleteAll(mediaFileList);
        }
        catch (Exception e) {
            throw new MediaFileException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FileResponseDTO getFileWithUrl(Integer id) {
        FileResponseDTO cachedFile = redisCommandService.getMediaFile(id);
        if (cachedFile != null) {
            return this.setFileUrl(cachedFile);
        }

        MediaFile result = mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaFileException(ErrorCode.MEDIA_FILE_NOT_FOUND));

        return mediaFileMapper.toResponseDTOWithUrl(result);
    }

    private FileResponseDTO setFileUrl(FileResponseDTO responseDTO) {
        responseDTO.setUrl(storageService.getFileUrl(responseDTO.getFileName()));
        return responseDTO;
    }
}
