package com.example.myflower.service.impl;

import com.example.myflower.dto.feedback.request.CreateFeedbackRequestDTO;
import com.example.myflower.dto.feedback.request.UpdateFeedbackRequestDTO;
import com.example.myflower.dto.feedback.response.FeedbackResponseDTO;
import com.example.myflower.dto.feedback.response.RatingFeedbackResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Feedback;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.RatingEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.feedback.FeedbackAppException;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.mapper.FeedbackMapper;
import com.example.myflower.repository.FeedbackRepository;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.FeedbackService;
import com.example.myflower.utils.AccountUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    @NonNull
    private FeedbackRepository feedbackRepository;
    @NonNull
    private FlowerListingRepository flowerListingRepository;
    @NonNull
    private FeedbackMapper feedbackMapper;

    @Override
    public FeedbackResponseDTO addFeedback(CreateFeedbackRequestDTO requestDTO) {
        Account currentUser = AccountUtils.getCurrentAccount();
        if (currentUser == null) {
            throw new FeedbackAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        //TODO: Validate only users buy the flower can add feedback

        FlowerListing flowerListing = flowerListingRepository.findByIdAndDeleteStatus(requestDTO.getFlowerId(), Boolean.FALSE)
                .orElseThrow(() -> new FeedbackAppException(ErrorCode.FLOWER_NOT_FOUND));

        boolean isExistFeedback = feedbackRepository.existsByFlowerIdAndUserIdAndIsDeletedFalse(requestDTO.getFlowerId(), currentUser.getId());
        if (isExistFeedback) {
            throw new FeedbackAppException(ErrorCode.USER_ALREADY_FEEDBACK);
        }

        RatingEnum rating = RatingEnum.valueOf(requestDTO.getRating());
        Feedback feedback = Feedback.builder()
                .user(currentUser)
                .flower(flowerListing)
                .description(requestDTO.getDescription())
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .build();
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return feedbackMapper.toResponseDTO(savedFeedback);
    }

    public FeedbackResponseDTO updateFeedback(Integer flowerId, UpdateFeedbackRequestDTO updateFeedbackRequestDTO) {
        return null;
    }

    @Override
    public List<FeedbackResponseDTO> getFeedbackByFlowerId(Integer flowerId) {
        List<Feedback> result = feedbackRepository.findAllByFlowerIdAndIsDeletedFalse(flowerId);
        return result.stream()
                .map(feedbackMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<FeedbackResponseDTO> getFeedbackBySeller(Integer sellerId) {
        List<FlowerListing> flowerListingList = flowerListingRepository.findByUserId(sellerId, Boolean.FALSE);
        List<Feedback> feedbackList = feedbackRepository
                .findAllByFlowerIdInAndIsDeletedFalse(
                        flowerListingList.stream()
                                .map(FlowerListing::getId)
                                .toList()
                );
        return feedbackList.stream()
                .map(feedbackMapper::toResponseDTOWithFlowerData)
                .toList();
    }

    @Override
    public void deleteFeedback(Integer id) {
        Account currentUser = AccountUtils.getCurrentAccount();
        if (currentUser == null) {
            throw new FeedbackAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Feedback feedback = feedbackRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new FeedbackAppException(ErrorCode.FEEDBACK_NOT_FOUND));
        if (currentUser.getRole() != AccountRoleEnum.ADMIN
                && !Objects.equals(currentUser.getId(), feedback.getUser().getId())) {
            throw new FeedbackAppException(ErrorCode.UNAUTHORIZED);
        }

        feedback.setDeleted(Boolean.TRUE);
        feedbackRepository.save(feedback);
    }

    @Override
    public void restoreFeedback(Integer id) {
        Account adminAccount = AccountUtils.getCurrentAccount();
        if (!AccountUtils.isAdminRole(adminAccount)) {
            throw new FlowerListingException(ErrorCode.UNAUTHORIZED);
        }

        Feedback feedback = feedbackRepository.findById(id).orElseThrow(() -> new FeedbackAppException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedback.setDeleted(Boolean.FALSE);
        feedbackRepository.save(feedback);
    }
    @Override
    public RatingFeedbackResponseDTO ratingByUserId(Integer accountId) {
        Integer count = feedbackRepository.countByFlower_UserId(accountId);
        List<Feedback> feedbacks = feedbackRepository.findAllByFlower_UserId(accountId);
        Double average = feedbacks.stream().mapToDouble(
                feedback -> feedback.getRating().getValue())
                .average()
                .orElse(0.0);

        return RatingFeedbackResponseDTO.builder()
                .countFeedback(count)
                .average(average)
                .build();
    }
}
