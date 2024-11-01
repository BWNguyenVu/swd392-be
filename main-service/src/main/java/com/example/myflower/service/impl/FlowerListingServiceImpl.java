package com.example.myflower.service.impl;

import com.example.myflower.consts.Constants;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.auth.requests.CreateFlowerListingRequestDTO;
import com.example.myflower.dto.auth.requests.GetFlowerListingsRequestDTO;
import com.example.myflower.dto.auth.requests.UpdateFlowerListingRequestDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.dto.flowerlisting.FlowerListingCacheDTO;
import com.example.myflower.dto.notification.NotificationMessageDTO;
import com.example.myflower.dto.pagination.PaginationResponseDTO;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.entity.*;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.DestinationScreenEnum;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.entity.enumType.NotificationTypeEnum;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.mapper.FlowerListingMapper;
import com.example.myflower.mapper.MediaFileMapper;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
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
    @Lazy
    private AccountService accountService;

    @NonNull
    @Lazy
    private FlowerCategoryService flowerCategoryService;

    @NonNull
    private FlowerListingRepository flowerListingRepository;

    @NonNull
    private FlowerCategoryRepository flowerCategoryRepository;

    @NonNull
    private FlowerImageRepository flowerImageRepository;

    @NonNull
    private SchedulerService schedulerService;

    @NonNull
    private FlowerListingMapper flowerListingMapper;

    @NonNull
    private MediaFileMapper mediaFileMapper;

    @NonNull
    private KafkaTemplate<String, NotificationMessageDTO> kafkaNotificationTemplate;

    @Override
    public PaginationResponseDTO<FlowerListingResponseDTO> getFlowerListings(GetFlowerListingsRequestDTO requestDTO)
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

        Account currentAccount = AccountUtils.getCurrentAccount();
        //If current user is admin then they can query delete status, otherwise can only get active flowers
        Boolean deleteStatus = Boolean.FALSE;
        FlowerListingStatusEnum flowerStatus = FlowerListingStatusEnum.APPROVED;
        if (currentAccount != null && currentAccount.getRole().equals(AccountRoleEnum.ADMIN)) {
            deleteStatus = requestDTO.getDeleteStatus();
            flowerStatus = requestDTO.getFlowerStatus();
        }
        //Get from database
        Page<FlowerListing> flowerListingsPage = flowerListingRepository.findAllByParameters(
                requestDTO.getSearchString(),
                requestDTO.getCategoryIds(),
                flowerStatus,
                deleteStatus,
                pageable
        );

        PaginationResponseDTO<FlowerListingResponseDTO> responseDTO = flowerListingMapper.toFlowerListingListResponseDTO(flowerListingsPage);

        LOG.info("[getFlowerListings] End with result: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public FlowerListingResponseDTO getFlowerListingByID(Integer id) {
        LOG.info("[getFlowerListingByID] Start get flower listing by ID: {}", id);

        Account currentAccount = AccountUtils.getCurrentAccount();
        Boolean isDeleted = null;
        if (!AccountUtils.isAdminRole(currentAccount)) {
            FlowerListingResponseDTO cacheResponseDTO = this.getCachedFlowerDetailsById(id);
            if (cacheResponseDTO != null) {
                return cacheResponseDTO;
            }
            isDeleted = Boolean.FALSE;
        }
        FlowerListing result = flowerListingRepository
                .findByIdAndDeleteStatus(id, isDeleted)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));
        FlowerListingCacheDTO cacheDTO = flowerListingMapper.toCacheDTO(result);
        redisCommandService.storeFlower(cacheDTO);
        FlowerListingResponseDTO responseDTO = flowerListingMapper.toFlowerListingResponseDTO(result);

        schedulerService.updateFlowerViews(id, 1);
        LOG.info("[getFlowerListingByID] End with response data: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public List<FlowerListingResponseDTO> getFlowerListingsByUserID(Integer userId) {
        LOG.info("[getFlowerListingByUserID] Start get flower listing by userID {}", userId);
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (!this.isHavingFlowerPermissions(currentAccount, userId)) {
            LOG.error("[getFlowerListingByUserID] Current user is unauthorized to access this resource");
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }
        List<FlowerListing> result = flowerListingRepository.findByUserId(userId);
        List<FlowerListingResponseDTO> responseDTO = result.stream()
                .map(flowerListingMapper::toFlowerListingResponseDTO)
                .toList();

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

        //Validate images
        List<MultipartFile> imageFileList = flowerListingRequestDTO.getImages();
        boolean allImagesValid = imageFileList.stream()
                .allMatch(ValidationUtils::validateImage);
        if (!allImagesValid) {
            throw new FlowerListingException(ErrorCode.INVALID_IMAGE);
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

        List<MediaFile> fileEntityList = fileMediaService.uploadMultipleFile(imageFileList);
        Set<FlowerImage> flowerImageList = fileEntityList.stream()
                .map(file -> FlowerImage.builder()
                        .flowerListing(flowerListing)
                        .mediaFile(file)
                        .build())
                .collect(Collectors.toSet());
        flowerListing.setImages(flowerImageList);
        FlowerListing result = flowerListingRepository.save(flowerListing);

        FlowerListingCacheDTO cacheDTO = flowerListingMapper.toCacheDTO(result);
        redisCommandService.storeFlower(cacheDTO);

        FlowerListingResponseDTO responseDTO = flowerListingMapper.toFlowerListingResponseDTO(result);

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
        if (!this.isHavingFlowerPermissions(account, flowerListing)) {
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
        List<MediaFile> deletedImages = flowerImageList.stream()
                .filter(image -> !remainingImages.contains(image))
                .map(FlowerImage::getMediaFile)
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

        List<MediaFile> fileEntityList = fileMediaService.uploadMultipleFile(imageFileList);

        Set<FlowerImage> images = fileEntityList.stream()
                .map(file -> FlowerImage.builder()
                        .flowerListing(flowerListing)
                        .mediaFile(file)
                        .build())
                .collect(Collectors.toSet());
        images.addAll(remainingImages);
        flowerListing.setImages(images);
        FlowerListing result = flowerListingRepository.save(flowerListing);

        //Delete all selected images in storage
        fileMediaService.deleteMultipleFiles(deletedImages);

        FlowerListingCacheDTO cacheDTO = flowerListingMapper.toCacheDTO(result);
        redisCommandService.storeFlower(cacheDTO);

        FlowerListingResponseDTO responseDTO = flowerListingMapper.toFlowerListingResponseDTO(result);

        LOG.info("[updateFlowerListing] End with new data: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public void deleteFlower(Integer id) {
        FlowerListing flowerListing = flowerListingRepository
                .findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (!this.isHavingFlowerPermissions(currentAccount, flowerListing)) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }
        flowerListing.setDeleted(Boolean.TRUE);
        flowerListingRepository.save(flowerListing);
        redisCommandService.deleteFlowerById(id);
    }

    @Override
    public void restoreFlower(Integer id) {
        FlowerListing flowerListing = flowerListingRepository
                .findById(id)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));
        Account currentAccount = AccountUtils.getCurrentAccount();
        if (!this.isHavingFlowerPermissions(currentAccount, flowerListing)) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }
        flowerListing.setDeleted(Boolean.FALSE);
        FlowerListing updatedFlower = flowerListingRepository.save(flowerListing);
        redisCommandService.storeFlower(flowerListingMapper.toCacheDTO(updatedFlower));
    }

    @Override
    public Integer countProductBySeller(Integer sellerId) {
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
        FlowerListing updatedFlower = flowerListingRepository.save(flowerListing.get());
        redisCommandService.storeFlower(flowerListingMapper.toCacheDTO(updatedFlower));
    }

    @Override
    public List<FileResponseDTO> getFlowerImages(Integer flowerId) {
        List<FlowerImage> flowerImageList = flowerImageRepository.findAllByFlowerListingId(flowerId);
        return flowerImageList.stream()
                .map(FlowerImage::getMediaFile) // Get MediaFile from FlowerImage
                .map(mediaFileMapper::toResponseDTOWithUrl)
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
         return mediaFileMapper.toResponseDTOWithUrl(file);
    }

    @Override
    public FlowerListingResponseDTO approveFlowerListing(Integer id) {
        Account adminAccount = AccountUtils.getCurrentAccount();

        if (!AccountUtils.isAdminRole(adminAccount)) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        FlowerListing flowerListing = flowerListingRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        flowerListing.setStatus(FlowerListingStatusEnum.APPROVED);
        flowerListing.setUpdatedAt(LocalDateTime.now());

        FlowerListing approvedListing = flowerListingRepository.save(flowerListing);
        //Push notification
        NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                .userId(flowerListing.getUser().getId())
                .title("Your flower has been approved")
                .message(flowerListing.getName() + " has been approved by admin!")
                .destinationScreen(DestinationScreenEnum.MY_FLOWER_LISTING)
                .type(NotificationTypeEnum.FLOWER_LISTING_STATUS)
                .build();
        kafkaNotificationTemplate.send("push_notification_topic", notificationMessageDTO);
        return flowerListingMapper.toFlowerListingResponseDTO(approvedListing);
    }

    @Override
    public FlowerListingResponseDTO rejectFlowerListing(Integer id, String reason) {
        Account adminAccount = AccountUtils.getCurrentAccount();

        //Check if the requester is admin
        if (!AccountUtils.isAdminRole(adminAccount)) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        FlowerListing flowerListing = flowerListingRepository.findByIdAndDeleteStatus(id, Boolean.FALSE)
                .orElseThrow(() -> new FlowerListingException(ErrorCode.FLOWER_NOT_FOUND));

        flowerListing.setStatus(FlowerListingStatusEnum.REJECTED);
        flowerListing.setRejectReason(reason);
        flowerListing.setUpdatedAt(LocalDateTime.now());

        FlowerListing rejectedListing = flowerListingRepository.save(flowerListing);

        //Push notification
        NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                .userId(flowerListing.getUser().getId())
                .title("Your flower has been rejected")
                .message(flowerListing.getName() + " has been rejected by admin with reason: " + flowerListing.getRejectReason())
                .destinationScreen(DestinationScreenEnum.MY_FLOWER_LISTING)
                .type(NotificationTypeEnum.FLOWER_LISTING_STATUS)
                .build();
        kafkaNotificationTemplate.send("push_notification_topic", notificationMessageDTO);
        return flowerListingMapper.toFlowerListingResponseDTO(rejectedListing);
    }

    @Override
    public void disableExpiredFlowers() {
        List<FlowerListing> expiredFlower = flowerListingRepository.findByExpireDateBefore(LocalDateTime.now());
        expiredFlower.forEach(flower -> flower.setStatus(FlowerListingStatusEnum.EXPIRED));
        flowerListingRepository.saveAll(expiredFlower);
        expiredFlower.forEach(flowerListing -> {
            //Push notification
            NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                    .userId(flowerListing.getUser().getId())
                    .title("Your flower has been expired!")
                    .message(flowerListing.getName() + " has been expired on " + flowerListing.getExpireDate())
                    .destinationScreen(DestinationScreenEnum.MY_FLOWER_LISTING)
                    .type(NotificationTypeEnum.FLOWER_LISTING_STATUS)
                    .build();
            kafkaNotificationTemplate.send("push_notification_topic", notificationMessageDTO);
        });
    }

    @Override
    public FlowerListingResponseDTO getCachedFlowerDetailsById(Integer id) {
        FlowerListingCacheDTO cacheDTO = redisCommandService.getFlowerById(id);
        AccountResponseDTO account = accountService.getProfileById(cacheDTO.getUserId());
        List<FlowerCategoryResponseDTO> categories = cacheDTO.getCategories().stream()
                .map(flowerCategoryService::getFlowerCategoryById)
                .toList();
        List<FileResponseDTO> images = cacheDTO.getImages().stream()
                .map(fileMediaService::getFileWithUrl)
                .toList();
        return flowerListingMapper.toResponseDTO(cacheDTO, account, categories, images);
    }

    private boolean isHavingFlowerPermissions(Account account, FlowerListing flower) {
        if (flower == null || account == null) {
            return false;
        }
        return AccountUtils.isAdminRole(account)
                || Objects.equals(flower.getUser().getId(), account.getId());
    }

    private boolean isHavingFlowerPermissions(Account account, Integer userId) {
        if (account == null) {
            return false;
        }
        return AccountUtils.isAdminRole(account)
                || Objects.equals(account.getId(), userId);
    }
}