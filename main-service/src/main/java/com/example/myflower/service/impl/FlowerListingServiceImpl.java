package com.example.myflower.service.impl;

import com.example.myflower.consts.Constants;
import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.GetFlowerListingsRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.responses.FlowerListingListResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.mapper.FlowerListingMapper;
import com.example.myflower.repository.FlowerCategoryRepository;
import com.example.myflower.repository.FlowerImageRepository;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.*;
import com.example.myflower.utils.AccountUtils;
import com.example.myflower.utils.FileUtils;
import com.example.myflower.utils.ValidationUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlowerListingServiceImpl implements FlowerListingService {
    private static final Logger LOG = LogManager.getLogger(FlowerListingServiceImpl.class);
    @NonNull
    private RedisCommandService redisCommandService;

    @NonNull
    private FileMediaService fileMediaService;

    @NonNull
    private StorageService storageService;

    @NonNull
    private FlowerListingRepository flowerListingRepository;

    @NonNull
    private FlowerCategoryRepository flowerCategoryRepository;

    @NonNull
    private FlowerImageRepository flowerImageRepository;

    @Autowired
    private SchedulerService schedulerService;

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
        Page<FlowerListing> flowerListingsPage = flowerListingRepository.findAllByParameters(
                requestDTO.getSearchString(),
                requestDTO.getCategoryIds(),
                FlowerListingStatusEnum.APPROVED,
                Boolean.FALSE,
                pageable
        );

        FlowerListingListResponseDTO responseDTO = FlowerListingMapper.toFlowerListingListResponseDTO(flowerListingsPage);
        //Map file name to storage url
        responseDTO.getContent()
                .forEach(flower -> flower.setImages(this.getFlowerImages(flower.getId())));
        LOG.info("[getFlowerListings] End with result: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public FlowerListingResponseDTO getFlowerListingByID(Integer id) {
        LOG.info("[getFlowerListingByID] Start get flower listing by ID: {}", id);

        schedulerService.updateFlowerViews(id, 1);

        FlowerListingResponseDTO cacheResponseDTO = redisCommandService.getFlowerById(id);
        if (cacheResponseDTO != null) {
            return cacheResponseDTO;
        }
        FlowerListing result = flowerListingRepository
                .findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));
        FlowerListingResponseDTO responseDTO = FlowerListingMapper.toFlowerListingResponseDTO(result);
        responseDTO.setImages(this.getFlowerImages(id));

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
        responseDTO.forEach(flower -> flower.setImages(this.getFlowerImages(flower.getId())));
        LOG.info("[getFlowerListingsByUserID] End with response data: {}", responseDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public FlowerListingResponseDTO createFlowerListing(CreateFlowerListingRequestDTO flowerListingRequestDTO, Account account) {
        LOG.info("[createFlowerListing] Start create new flower listing with data: {}", flowerListingRequestDTO);
        // Fetch categories by their IDs
        List<FlowerCategory> categories = flowerCategoryRepository.findByIdIn(flowerListingRequestDTO.getCategories());
        LOG.info("[createFlowerListing] Found flower categories: {}", categories);
        List<MultipartFile> imageFileList = flowerListingRequestDTO.getImages();

        for (MultipartFile imageFile : imageFileList) {
            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
            }
        }

        FlowerListing flowerListing = FlowerListing
                .builder()
                .name(flowerListingRequestDTO.getName())
                .description(flowerListingRequestDTO.getDescription())
                .user(account)
                .address(flowerListingRequestDTO.getAddress())
                .price(flowerListingRequestDTO.getPrice())
                .stockQuantity(flowerListingRequestDTO.getStockQuantity())
                .categories(new HashSet<>(categories))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(FlowerListingStatusEnum.PENDING)
                .isDeleted(Boolean.FALSE)
                .build();
        FlowerListing result = flowerListingRepository.save(flowerListing);

        List<MediaFile> fileEntityList = fileMediaService.uploadMultipleFile(imageFileList);
        List<FlowerImage> flowerImageEntityList = new ArrayList<>();
        fileEntityList.forEach(fileEntity -> {
            FlowerImage flowerImage = FlowerImage.builder()
                    .mediaFile(fileEntity)
                    .flowerListing(flowerListing)
                    .build();
            flowerImageEntityList.add(flowerImage);
        });
        flowerImageRepository.saveAll(flowerImageEntityList);

        FlowerListingResponseDTO responseDTO = FlowerListingMapper.toFlowerListingResponseDTO(result);
        List<FileResponseDTO> fileResponseList = fileEntityList.stream()// Get MediaFile from FlowerImage
                .map(mediaFile -> FileResponseDTO.builder() // Create FileResponseDTO
                        .id(mediaFile.getId()) // Set the ID from MediaFile
                        .url(storageService.getFileUrl(mediaFile.getFileName())) // Set the URL
                        .build())
                .toList();
        responseDTO.setImages(fileResponseList);

        redisCommandService.setFlowerById(responseDTO);
        LOG.info("[createFlowerListing] End with result: {}", responseDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public FlowerListingResponseDTO updateFlowerListing(Integer id, Account account, UpdateFlowerListingRequestDTO flowerListingRequestDTO) {
        LOG.info("[updateFlowerListing] Start update flower listing by ID {} and payload: {}", id, flowerListingRequestDTO);
        FlowerListing flowerListing = flowerListingRepository
                .findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        //If request user is not owner of flower listing or not admin then throw error
        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !Objects.equals(flowerListing.getUser().getId(), account.getId())) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        List<MultipartFile> imageFileList = FileUtils.filterEmptyFiles(flowerListingRequestDTO.getNewImages());
        for (MultipartFile imageFile : imageFileList) {
            if (!ValidationUtils.validateImage(imageFile)) {
                throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
            }
        }

        List<FlowerImage> flowerImageList = flowerImageRepository.findAllByFlowerListingId(id);
        List<FlowerImage> remainingImages = flowerImageList.stream()
                .filter(image -> !flowerListingRequestDTO.getDeletedImages().contains(image.getMediaFile().getId()))
                .toList();
        // Check if the remainingImages list is empty and the newImages list is also empty
        if (remainingImages.isEmpty() && imageFileList.isEmpty()) {
            throw new FlowerListingException(ErrorCode.NO_IMAGE_LEFT);
        }

        flowerListing.setName(flowerListingRequestDTO.getName());
        flowerListing.setDescription(flowerListingRequestDTO.getDescription());
        flowerListing.setAddress(flowerListingRequestDTO.getAddress());
        flowerListing.setPrice(flowerListingRequestDTO.getPrice());
        flowerListing.setStockQuantity(flowerListingRequestDTO.getStockQuantity());
        flowerListing.setUpdatedAt(LocalDateTime.now());
        flowerListing.setStatus(FlowerListingStatusEnum.PENDING);

        FlowerListing updatedFlowerListing = flowerListingRepository.save(flowerListing);
        //Save new flower image entities
        List<FlowerImage> flowerImageEntityList = new ArrayList<>();
        List<MediaFile> fileEntityList = fileMediaService.uploadMultipleFile(imageFileList);
        fileEntityList.forEach(fileEntity -> {
            FlowerImage flowerImage = FlowerImage.builder()
                    .mediaFile(fileEntity)
                    .flowerListing(flowerListing)
                    .build();
            flowerImageEntityList.add(flowerImage);
        });
        flowerImageRepository.saveAll(flowerImageEntityList);
        //Delete old flower image entities
        flowerImageRepository.deleteAllByMediaFileIdIn(flowerListingRequestDTO.getDeletedImages());

        FlowerListingResponseDTO responseDTO = FlowerListingMapper.toFlowerListingResponseDTO(updatedFlowerListing);
        responseDTO.setImages(this.getFlowerImages(id));

        redisCommandService.setFlowerById(responseDTO);
        LOG.info("[updateFlowerListing] End with new data: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public Integer countProductBySeller(Integer sellerId){
        return flowerListingRepository.countFlowerListingByUserIdAndStatusNotIn(sellerId, List.of(FlowerListingStatusEnum.PENDING, FlowerListingStatusEnum.REJECTED) );
    }

    @Override
    public void clearFlowerListingCache() {
        LOG.info("[clearFlowerListingCache] Start clear flower listing cache");
        redisCommandService.clearFlowerCache();
        LOG.info("[clearFlowerListingCache] Finished clear cache");
    }

    @Override
    public void updateViewsFlowerListing(Integer flowerListingId, Integer views) {
        Optional<FlowerListing> flowerListing = flowerListingRepository.findById(flowerListingId);
        if (flowerListing.isPresent()) {
            flowerListing.get().setViews(flowerListing.get().getViews() + views);
        } else {
            throw new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND);
        }
        flowerListingRepository.save(flowerListing.get());
    }

    @Override
    public List<FileResponseDTO> getFlowerImages(Integer flowerId) {
        List<FlowerImage> flowerImageList = flowerImageRepository.findAllByFlowerListingId(flowerId);
        return flowerImageList.stream()
                .map(FlowerImage::getMediaFile) // Get MediaFile from FlowerImage
                .map(mediaFile -> FileResponseDTO.builder() // Create FileResponseDTO
                        .id(mediaFile.getId()) // Set the ID from MediaFile
                        .url(storageService.getFileUrl(mediaFile.getFileName())) // Set the URL
                        .build())
                .toList();
    }

    @Override
    public FileResponseDTO getFeaturedFlowerImage(Integer flowerId) {
        List<FlowerImage> flowerImageList = flowerImageRepository.findAllByFlowerListingId(flowerId);
        //TODO: IMPLEMENT SORT ORDER TO GET IMAGE WITH FIRST ORDER
         MediaFile file = flowerImageList.stream()
                 .map(FlowerImage::getMediaFile)
                 .findFirst()
                 .orElseThrow(() -> new FlowerListingException(ErrorCode.INTERNAL_SERVER_ERROR));
         return FileResponseDTO.builder()
                 .id(file.getId())
                 .url(storageService.getFileUrl(file.getFileName()))
                 .build();
    }
}
