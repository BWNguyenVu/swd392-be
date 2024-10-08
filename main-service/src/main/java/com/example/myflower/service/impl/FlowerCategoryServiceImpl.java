package com.example.myflower.service.impl;

import com.example.myflower.dto.flowercategogy.request.CreateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.request.UpdateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.FlowerCategory;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerCategoryException;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.mapper.FlowerCategoryMapper;
import com.example.myflower.repository.FlowerCategoryRepository;
import com.example.myflower.service.FlowerCategoryService;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.ValidationUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerCategoryServiceImpl implements FlowerCategoryService {
    @NonNull
    private RedisCommandService redisCommandService;

    @NonNull
    private StorageService storageService;

    @NonNull
    private FlowerCategoryRepository flowerCategoryRepository;

    @Override
    @Transactional
    public FlowerCategoryResponseDTO createFlowerCategory(CreateFlowerCategoryRequestDTO requestDTO) {
        try {
            MultipartFile imageFile = requestDTO.getImage();
            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerCategoryException(ErrorCode.INVALID_IMAGE);
            }

            //Store image at file storage
            String fileName = storageService.uploadFile(imageFile);

            FlowerCategory flowerCategory = FlowerCategory.builder()
                    .name(requestDTO.getName())
                    .categoryParent(requestDTO.getParentCategory())
                    .imageUrl(fileName)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            FlowerCategory result = flowerCategoryRepository.save(flowerCategory);
            result.setImageUrl(storageService.getFileUrl(fileName));
            FlowerCategoryResponseDTO responseDTO = FlowerCategoryMapper.toCategoryResponseDTO(result);
            redisCommandService.setFlowerCategoryById(responseDTO);
            return responseDTO;
        }
        catch (IOException e) {
            throw new FlowerCategoryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
        responseDTOList.forEach(responseDTO -> responseDTO.setImageUrl(storageService.getFileUrl(responseDTO.getImageUrl())));
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
        result.setImageUrl(storageService.getFileUrl(result.getImageUrl()));
        FlowerCategoryResponseDTO responseDTO = FlowerCategoryMapper.toCategoryResponseDTO(result);
        //Save to cache
        redisCommandService.setFlowerCategoryById(responseDTO);
        return responseDTO;
    }

    @Override
    public FlowerCategoryResponseDTO updateFlowerCategoryById(Integer id, UpdateFlowerCategoryRequestDTO requestDTO) {
        try {
            MultipartFile imageFile = requestDTO.getImage();
            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
            }

            FlowerCategory flowerCategory = flowerCategoryRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                    .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));


            //Store image at file storage
            String fileName = storageService.uploadFile(imageFile);
            //Update fields
            flowerCategory.setName(requestDTO.getName());
            flowerCategory.setCategoryParent(requestDTO.getParentCategory());
            flowerCategory.setImageUrl(fileName);
            flowerCategory.setUpdatedAt(LocalDateTime.now());
            //Save to database
            FlowerCategory result = flowerCategoryRepository.save(flowerCategory);
            //Delete old image
            storageService.deleteFile(fileName);
            result.setImageUrl(storageService.getFileUrl(fileName));
            FlowerCategoryResponseDTO responseDTO = FlowerCategoryMapper.toCategoryResponseDTO(result);
            //Save to cache
            redisCommandService.setFlowerCategoryById(responseDTO);
            return responseDTO;
        }
        catch (IOException e) {
            throw new FlowerCategoryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
