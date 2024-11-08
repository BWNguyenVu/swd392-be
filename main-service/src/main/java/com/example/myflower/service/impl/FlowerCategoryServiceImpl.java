package com.example.myflower.service.impl;

import com.example.myflower.dto.flowercategogy.request.CreateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.request.UpdateFlowerCategoryRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerCategory;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerCategoryException;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.mapper.FlowerCategoryMapper;
import com.example.myflower.repository.FlowerCategoryRepository;
import com.example.myflower.service.FlowerCategoryService;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.AccountUtils;
import com.example.myflower.utils.ValidationUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerCategoryServiceImpl implements FlowerCategoryService {
    private static final Logger LOG = LogManager.getLogger(FlowerCategoryServiceImpl.class);
    @NonNull
    private RedisCommandService redisCommandService;

    @NonNull
    private StorageService storageService;

    @NonNull
    private FlowerCategoryRepository flowerCategoryRepository;

    @NonNull
    private FlowerCategoryMapper flowerCategoryMapper;

    @Override
    @Transactional
    public FlowerCategoryResponseDTO createFlowerCategory(CreateFlowerCategoryRequestDTO requestDTO) {
        try {
            Account adminAccount = AccountUtils.getCurrentAccount();
            if (!AccountUtils.isAdminRole(adminAccount)) {
                throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
            }

            MultipartFile imageFile = requestDTO.getImage();
            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerCategoryException(ErrorCode.INVALID_IMAGE);
            }

            LOG.info("[createFlowerCategory]: Start creating flower category with request data {}", requestDTO);
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
            FlowerCategoryResponseDTO responseDTO = flowerCategoryMapper.toCategoryResponseDTO(result);
            redisCommandService.setFlowerCategoryById(responseDTO);
            return responseDTO;
        }
        catch (IOException e) {
            LOG.info("[createFlowerCategory]: Has exception {}", requestDTO);
            throw new FlowerCategoryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<FlowerCategoryResponseDTO> getAllFlowerCategory() {
        Account adminAccount = AccountUtils.getCurrentAccount();
        Boolean isDeleted = null;
        if (!AccountUtils.isAdminRole(adminAccount)) {
            //Get form cache
            List<FlowerCategoryResponseDTO> cacheResponseDTOList = redisCommandService.getAllFlowerCategoriesWithDeleteStatusFalse();
            if (!cacheResponseDTOList.isEmpty()) {
                return cacheResponseDTOList;
            }
            isDeleted = Boolean.FALSE;
        }
        //Get from database
        List<FlowerCategory> results = flowerCategoryRepository.findAllByDeleteStatus(isDeleted);
        List<FlowerCategoryResponseDTO> responseDTOList = results.stream()
                .map(flowerCategoryMapper::toCategoryResponseDTO)
                .toList();
        //Save to cache
        if (!responseDTOList.isEmpty() && Boolean.FALSE.equals(isDeleted)) {
            redisCommandService.setAllFlowerCategoriesWithDeleteStatusFalse(responseDTOList);
        }
        return responseDTOList;
    }

    @Override
    public FlowerCategoryResponseDTO getFlowerCategoryById(Integer id) {
        Account adminAccount = AccountUtils.getCurrentAccount();
        Boolean isDeleted = null;
        if (!AccountUtils.isAdminRole(adminAccount)) {
            FlowerCategoryResponseDTO cacheResponseDTO = redisCommandService.getFlowerCategoryById(id);
            if (cacheResponseDTO != null) {
                return cacheResponseDTO;
            }
            isDeleted = Boolean.FALSE;
        }
        FlowerCategory result = flowerCategoryRepository.findByIdAndDeleteStatus(id, isDeleted)
                .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));
        FlowerCategoryResponseDTO responseDTO = flowerCategoryMapper.toCategoryResponseDTO(result);
        if (Boolean.FALSE.equals(isDeleted)) {
            //Save to cache
            redisCommandService.setFlowerCategoryById(responseDTO);
        }
        return responseDTO;
    }

    @Override
    public FlowerCategoryResponseDTO updateFlowerCategoryById(Integer id, UpdateFlowerCategoryRequestDTO requestDTO) {
        try {
            Account adminAccount = AccountUtils.getCurrentAccount();
            if (!AccountUtils.isAdminRole(adminAccount)) {
                throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
            }

            FlowerCategory flowerCategory = flowerCategoryRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                    .orElseThrow(() -> new FlowerCategoryException(ErrorCode.FLOWER_CATEGORY_NOT_FOUND));


            String removedImage = null;
            if (requestDTO.getImage() != null) {
                removedImage = flowerCategory.getImageUrl();
                MultipartFile imageFile = requestDTO.getImage();
                if (!ValidationUtils.validateImage(imageFile)) {
                    throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
                }
                //Store image at file storage
                String fileName = storageService.uploadFile(imageFile);
                flowerCategory.setImageUrl(fileName);
            }
            //Update fields
            flowerCategory.setName(requestDTO.getName());
            flowerCategory.setCategoryParent(requestDTO.getParentCategory());
            flowerCategory.setUpdatedAt(LocalDateTime.now());
            //Save to database
            FlowerCategory result = flowerCategoryRepository.save(flowerCategory);
            FlowerCategoryResponseDTO responseDTO = flowerCategoryMapper.toCategoryResponseDTO(result);
            if (removedImage != null) {
                //Delete old image
                storageService.deleteFile(removedImage);
            }
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
        Account adminAccount = AccountUtils.getCurrentAccount();
        if (!AccountUtils.isAdminRole(adminAccount)) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

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
        redisCommandService.setFlowerCategoryById(flowerCategoryMapper.toCategoryResponseDTO(result));
    }

    @Override
    public void clearCategoryCache() {
        redisCommandService.clearFlowerCategoryCache();
    }
}
