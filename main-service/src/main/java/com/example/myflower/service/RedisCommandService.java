package com.example.myflower.service;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;

import java.util.List;

public interface RedisCommandService {
    List<FlowerCategoryResponseDTO> getAllFlowerCategoriesWithDeleteStatusFalse();

    FlowerCategoryResponseDTO getFlowerCategoryById(Integer id);

    void setAllFlowerCategoriesWithDeleteStatusFalse(List<FlowerCategoryResponseDTO> list);

    void setFlowerCategoryById(FlowerCategoryResponseDTO flowerCategoryResponseDTO);

    void deleteFlowerCategoryById(Integer id);

    // Method to get a flower listing by its ID
    FlowerListingResponseDTO getFlowerById(Integer id);

    // Method to set a single flower listing by its ID
    void setFlowerById(FlowerListingResponseDTO responseDTO);

    // Method to delete a flower listing by its ID
    void deleteFlowerById(Integer id);

    void storeRefreshToken(Integer userId, String refreshToken);

    boolean isRefreshTokenExisted(Integer userId, String refreshToken);

    String getValidRefreshTokenByUserId(Integer userId);

    void revokeRefreshToken(Integer userId);

    boolean isRevokedTokenExist(Integer userId, String refreshToken);
}
