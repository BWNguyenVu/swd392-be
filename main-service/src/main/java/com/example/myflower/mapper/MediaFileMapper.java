package com.example.myflower.mapper;

import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.entity.MediaFile;
import com.example.myflower.service.StorageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaFileMapper {
    @NonNull
    private StorageService storageService;

    public FileResponseDTO toResponseDTOWithUrl(MediaFile file) {
        String url = storageService.getFileUrl(file.getFileName());
        return FileResponseDTO.builder()
                .id(file.getId())
                .url(url)
                .fileName(file.getFileName())
                .build();
    }

    public FileResponseDTO toResponseDTO(MediaFile file) {
        return FileResponseDTO.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .build();
    }
}
