package com.example.myflower.service;

import com.example.myflower.dto.flowercategogy.request.CreateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.request.UpdateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;

import java.util.List;

public interface FlowerCategoryService {
    FlowerCategoryResponseDTO createFlowerCategory(CreateFlowerCategoryRequestDTO requestDTO);
    List<FlowerCategoryResponseDTO> getAllFlowerCategory();
    FlowerCategoryResponseDTO getFlowerCategoryById(Integer id);
    FlowerCategoryResponseDTO updateFlowerCategoryById(Integer id, UpdateFlowerCategoryRequestDTO requestDTO);
    void deleteFlowerCategoryById(Integer id);

    void restoreFlowerCategoryById(Integer id);

    void clearCategoryCache();
}
