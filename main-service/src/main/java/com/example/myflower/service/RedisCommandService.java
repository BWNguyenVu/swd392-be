package com.example.myflower.service;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.dto.flowerlisting.FlowerListingCacheDTO;

import java.util.List;

public interface RedisCommandService {
    List<FlowerCategoryResponseDTO> getAllFlowerCategoriesWithDeleteStatusFalse();

    FlowerCategoryResponseDTO getFlowerCategoryById(Integer id);

    void setAllFlowerCategoriesWithDeleteStatusFalse(List<FlowerCategoryResponseDTO> list);

    void setFlowerCategoryById(FlowerCategoryResponseDTO flowerCategoryResponseDTO);

    void deleteFlowerCategoryById(Integer id);

    // Method to get a flower listing by its ID
    FlowerListingCacheDTO getFlowerById(Integer id);

    // Method to set a single flower listing by its ID
    void setFlowerById(FlowerListingResponseDTO responseDTO);

    void storeFlower(FlowerListingCacheDTO cacheDTO);

    // Method to delete a flower listing by its ID
    void deleteFlowerById(Integer id);

    void storeRefreshToken(Integer userId, String refreshToken);

    boolean isRefreshTokenExisted(Integer userId, String refreshToken);

    String getValidRefreshTokenByUserId(Integer userId);

    void revokeRefreshToken(Integer userId);

    boolean isRevokedTokenExist(Integer userId, String refreshToken);

    void storePresignedUrl(String fileName, String presignedUrl);

    String getPresignedUrl(String fileName);

    void storeMediaFile(FileResponseDTO file);

    FileResponseDTO getMediaFile(Integer id);

    void storeOtpChangeEmail(Integer userId, String newEmail, String changeEmail);

    String getOtpChangeEmail(Integer userId, String changeEmail);

    void deleteOtp(Integer userId, String newEmail, String changeEmail);

    void clearFlowerCache();

    void clearFlowerCategoryCache();

    void clearFeedbackCache();

    void clearPresignedUrlCache();
}
