package com.example.myflower.service.impl;

import com.example.myflower.consts.Constants;
import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.GetFlowerListingsRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.responses.FlowerListingListResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerCategory;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.mapper.FlowerListingMapper;
import com.example.myflower.repository.FlowerCategoryRepository;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.FlowerListingService;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.StorageService;
import com.example.myflower.utils.AccountUtils;
import com.example.myflower.utils.ValidationUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FlowerListingServiceImpl implements FlowerListingService {
    private static final Logger LOG = LogManager.getLogger(FlowerListingServiceImpl.class);
    @NonNull
    private RedisCommandService redisCommandService;

    @NonNull
    private StorageService storageService;

    @NonNull
    private FlowerListingRepository flowerListingRepository;

    @NonNull
    private FlowerCategoryRepository flowerCategoryRepository;

    @Override
    public FlowerListingListResponseDTO getFlowerListings(GetFlowerListingsRequestDTO requestDTO)
    {
        LOG.info("[getFlowerListings] Start get flower listing by parameters with request {}", requestDTO);
        //Construct sort by field parameters
        Sort sort;
        switch (requestDTO.getSortBy()) {
            case Constants.SORT_FLOWER_LISTING_BY_NAME:
                sort = Sort.by(Constants.SORT_FLOWER_LISTING_BY_NAME);
                break;
            case Constants.SORT_FLOWER_LISTING_BY_PRICE:
                sort = Sort.by(Constants.SORT_FLOWER_LISTING_BY_PRICE);
                break;
            default:
                sort = Sort.by(Constants.SORT_FLOWER_LISTING_BY_CREATE_DATE);
                break;
        }
        //Construct sort order parameters
        if (Constants.SORT_ORDER_DESCENDING.equals(requestDTO.getOrder())) {
            sort = sort.descending();
        }
        //Construct pagination and sort parameters
        Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize(), sort);
        //Set null to bypass filter if request filter is empty
        if (requestDTO.getCategoryIds() != null && requestDTO.getCategoryIds().isEmpty()) {
            requestDTO.setCategoryIds(null);
        }
        //Get from database
        Page<FlowerListing> flowerListingsPage = flowerListingRepository.findAllByParameters(requestDTO.getSearchString(), requestDTO.getCategoryIds(), Boolean.FALSE, pageable);

        FlowerListingListResponseDTO responseDTO = FlowerListingMapper.toFlowerListingListResponseDTO(flowerListingsPage);
        //Map file name to storage url
        responseDTO.getContent()
                .forEach(flower -> flower.setImageUrl(storageService.getFileUrl(flower.getImageUrl())));
        LOG.info("[getFlowerListings] End with result: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public FlowerListingResponseDTO getFlowerListingByID(Integer id) {
        LOG.info("[getFlowerListingByID] Start get flower listing by ID: {}", id);
        FlowerListingResponseDTO cacheResponseDTO = redisCommandService.getFlowerById(id);
        if (cacheResponseDTO != null) {
            return cacheResponseDTO;
        }
        FlowerListing result = flowerListingRepository
                .findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));
        FlowerListingResponseDTO responseDTO = FlowerListingMapper.toFlowerListingResponseDTO(result);
        responseDTO.setImageUrl(storageService.getFileUrl(result.getImageUrl()));
        redisCommandService.setFlowerById(responseDTO);
        LOG.info("[getFlowerListingByID] End with response data: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public List<FlowerListingResponseDTO> getFlowerListingsByUserID(Integer userId) {
        LOG.info("[getFlowerListingByUserID] Start get flower listing by userID {}", userId);
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (!(currentAccount != null && (AccountRoleEnum.ADMIN.equals(currentAccount.getRole()) || userId.equals(currentAccount.getId())))) {
            LOG.error("[getFlowerListingByUserID] Current user is unauthorized to access this resource");
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }
        List<FlowerListing> result = flowerListingRepository.findByUserId(userId);
        List<FlowerListingResponseDTO> responseDTO = result.stream()
                .map(FlowerListingMapper::toFlowerListingResponseDTO)
                .toList();
        //Map file name to storage url
        responseDTO.forEach(flower -> flower.setImageUrl(storageService.getFileUrl(flower.getImageUrl())));
        LOG.info("[getFlowerListingsByUserID] End with response data: {}", responseDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public FlowerListingResponseDTO createFlowerListing(CreateFlowerListingRequestDTO flowerListingRequestDTO, Account account) {
        try {
            LOG.info("[createFlowerListing] Start create new flower listing with data: {}", flowerListingRequestDTO);
            // Fetch categories by their IDs
            List<FlowerCategory> categories = flowerCategoryRepository.findByIdIn(flowerListingRequestDTO.getCategories());
            LOG.info("[createFlowerListing] Found flower categories: {}", categories);
            MultipartFile imageFile = flowerListingRequestDTO.getImage();

            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
            }

            //Store image at file storage
            String fileName = storageService.uploadFile(imageFile);

            FlowerListing flowerListing = FlowerListing
                    .builder()
                    .name(flowerListingRequestDTO.getName())
                    .description(flowerListingRequestDTO.getDescription())
                    .user(account)
                    .address(flowerListingRequestDTO.getAddress())
                    .price(flowerListingRequestDTO.getPrice())
                    .stockQuantity(flowerListingRequestDTO.getStockQuantity())
                    .categories(new HashSet<>(categories))
                    .imageUrl(fileName)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(FlowerListingStatusEnum.PENDING)
                    .isDeleted(Boolean.FALSE)
                    .build();
            FlowerListing result = flowerListingRepository.save(flowerListing);

            FlowerListingResponseDTO responseDTO = FlowerListingMapper.toFlowerListingResponseDTO(result);
            responseDTO.setImageUrl(storageService.getFileUrl(fileName));
            redisCommandService.setFlowerById(responseDTO);
            LOG.info("[createFlowerListing] End with result: {}", responseDTO);
            return responseDTO;
        }
        catch (IOException e) {
            LOG.error("[createFlowerListing] Has exception: ", e);
            throw new FlowerListingException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FlowerListingResponseDTO updateFlowerListing(Integer id, Account account, UpdateFlowerListingRequestDTO flowerListingRequestDTO) {
        try {
            LOG.info("[updateFlowerListing] Start update flower listing by ID {} and payload: {}", id, flowerListingRequestDTO);
            FlowerListing flowerListing = flowerListingRepository
                    .findById(id)
                    .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

            //If request user is not owner of flower listing or not admin then throw error
            if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !Objects.equals(flowerListing.getUser().getId(), account.getId())) {
                throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
            }

            MultipartFile imageFile = flowerListingRequestDTO.getImage();
            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
            }
            //Store image at file storage
            String fileName = storageService.uploadFile(imageFile);

            flowerListing.setName(flowerListingRequestDTO.getName());
            flowerListing.setDescription(flowerListingRequestDTO.getDescription());
            flowerListing.setAddress(flowerListingRequestDTO.getAddress());
            flowerListing.setPrice(flowerListingRequestDTO.getPrice());
            flowerListing.setStockQuantity(flowerListingRequestDTO.getStockQuantity());
            flowerListing.setImageUrl(fileName);
            flowerListing.setUpdatedAt(LocalDateTime.now());
            flowerListing.setStatus(FlowerListingStatusEnum.PENDING);

            FlowerListing updatedFlowerListing = flowerListingRepository.save(flowerListing);

            FlowerListingResponseDTO responseDTO = FlowerListingMapper.toFlowerListingResponseDTO(updatedFlowerListing);
            responseDTO.setImageUrl(storageService.getFileUrl(fileName));
            redisCommandService.setFlowerById(responseDTO);
            LOG.info("[updateFlowerListing] End with new data: {}", responseDTO);
            return responseDTO;
        }
        catch (IOException e) {
            throw new FlowerListingException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
