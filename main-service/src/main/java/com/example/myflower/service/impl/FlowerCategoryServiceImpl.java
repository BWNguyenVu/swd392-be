package com.example.myflower.service.impl;

import com.example.myflower.dto.flowercategogy.request.CreateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.request.UpdateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.FlowerCategory;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerCategoryException;
import com.example.myflower.mapper.FlowerCategoryMapper;
import com.example.myflower.repository.FlowerCategoryRepository;
import com.example.myflower.service.FlowerCategoryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerCategoryServiceImpl implements FlowerCategoryService {
    @NonNull
    private FlowerCategoryRepository flowerCategoryRepository;

    @Override
    public FlowerCategoryResponseDTO createFlowerCategory(CreateFlowerCategoryRequestDTO requestDTO) {
        FlowerCategory flowerCategory = FlowerCategory.builder()
                .name(requestDTO.getName())
                .categoryParent(requestDTO.getParentCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        FlowerCategory result = flowerCategoryRepository.save(flowerCategory);
        return FlowerCategoryMapper.toCategoryResponseDTO(result);
    }

    @Override
    public List<FlowerCategoryResponseDTO> getAllFlowerCategory() {
        List<FlowerCategory> results = flowerCategoryRepository.findAllByDeleteStatus(Boolean.FALSE);
        return results.stream()
                .map(FlowerCategoryMapper::toCategoryResponseDTO)
                .toList();
    }

    @Override
    public FlowerCategoryResponseDTO getFlowerCategoryById(Integer id) {
        FlowerCategory result = flowerCategoryRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));
        return FlowerCategoryMapper.toCategoryResponseDTO(result);
    }

    @Override
    public FlowerCategoryResponseDTO updateFlowerCategoryById(Integer id, UpdateFlowerCategoryRequestDTO requestDTO) {
        FlowerCategory flowerCategory = flowerCategoryRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));

        flowerCategory.setName(requestDTO.getName());
        flowerCategory.setCategoryParent(requestDTO.getParentCategory());
        flowerCategory.setUpdatedAt(LocalDateTime.now());

        flowerCategoryRepository.save(flowerCategory);
        return FlowerCategoryMapper.toCategoryResponseDTO(flowerCategory);
    }

    @Override
    public void deleteFlowerCategoryById(Integer id) {
        FlowerCategory flowerCategory = flowerCategoryRepository.findById(id)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));

        //Soft delete
        flowerCategory.setDeleted(Boolean.TRUE);
        flowerCategory.setUpdatedAt(LocalDateTime.now());
        flowerCategoryRepository.save(flowerCategory);
    }

    @Override
    public void restoreFlowerCategoryById(Integer id) {
        FlowerCategory flowerCategory = flowerCategoryRepository.findById(id)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));

        //Restore delete status
        flowerCategory.setDeleted(Boolean.FALSE);
        flowerCategory.setUpdatedAt(LocalDateTime.now());
        flowerCategoryRepository.save(flowerCategory);
    }
}
