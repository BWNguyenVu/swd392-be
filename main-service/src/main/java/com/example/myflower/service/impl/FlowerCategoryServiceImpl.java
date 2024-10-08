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
import com.example.myflower.service.RedisCommandService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerCategoryServiceImpl implements FlowerCategoryService {
    @NonNull
    private RedisCommandService redisCommandService;

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
        FlowerCategoryResponseDTO responseDTO = FlowerCategoryMapper.toCategoryResponseDTO(result);
        redisCommandService.setFlowerCategoryById(responseDTO);
        return responseDTO;
    }

    @Override
    public List<FlowerCategoryResponseDTO> getAllFlowerCategory() {
        //Get form cache
        List<FlowerCategoryResponseDTO> cacheResponseDTOList = redisCommandService.getAllFlowerCategoriesWithDeleteStatusFalse();
        if (!cacheResponseDTOList.isEmpty()) {
            return cacheResponseDTOList;
        }

        //Get from database
        List<FlowerCategory> results = flowerCategoryRepository.findAllByDeleteStatus(Boolean.FALSE);
        List<FlowerCategoryResponseDTO> responseDTOList = results.stream()
                .map(FlowerCategoryMapper::toCategoryResponseDTO)
                .toList();
        //Save to cache
        if (!responseDTOList.isEmpty()) {
            redisCommandService.setAllFlowerCategoriesWithDeleteStatusFalse(responseDTOList);
        }
        return responseDTOList;
    }

    @Override
    public FlowerCategoryResponseDTO getFlowerCategoryById(Integer id) {
        FlowerCategoryResponseDTO cacheResponseDTO = redisCommandService.getFlowerCategoryById(id);
        if (cacheResponseDTO != null) {
            return cacheResponseDTO;
        }
        FlowerCategory result = flowerCategoryRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));
        FlowerCategoryResponseDTO responseDTO = FlowerCategoryMapper.toCategoryResponseDTO(result);
        //Save to cache
        redisCommandService.setFlowerCategoryById(responseDTO);
        return responseDTO;
    }

    @Override
    public FlowerCategoryResponseDTO updateFlowerCategoryById(Integer id, UpdateFlowerCategoryRequestDTO requestDTO) {
        FlowerCategory flowerCategory = flowerCategoryRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));

        //Update fields
        flowerCategory.setName(requestDTO.getName());
        flowerCategory.setCategoryParent(requestDTO.getParentCategory());
        flowerCategory.setUpdatedAt(LocalDateTime.now());
        //Save to database
        FlowerCategory result = flowerCategoryRepository.save(flowerCategory);
        FlowerCategoryResponseDTO responseDTO = FlowerCategoryMapper.toCategoryResponseDTO(result);
        //Save to cache
        redisCommandService.setFlowerCategoryById(responseDTO);
        return responseDTO;
    }

    @Override
    public void deleteFlowerCategoryById(Integer id) {
        FlowerCategory flowerCategory = flowerCategoryRepository.findById(id)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));

        //Soft delete
        flowerCategory.setDeleted(Boolean.TRUE);
        flowerCategory.setUpdatedAt(LocalDateTime.now());
        flowerCategoryRepository.save(flowerCategory);
        //Delete from cache
        redisCommandService.deleteFlowerCategoryById(id);
    }

    @Override
    public void restoreFlowerCategoryById(Integer id) {
        FlowerCategory flowerCategory = flowerCategoryRepository.findById(id)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));

        //Restore delete status
        flowerCategory.setDeleted(Boolean.FALSE);
        flowerCategory.setUpdatedAt(LocalDateTime.now());
        FlowerCategory result = flowerCategoryRepository.save(flowerCategory);
        redisCommandService.setFlowerCategoryById(FlowerCategoryMapper.toCategoryResponseDTO(result));
    }
}
