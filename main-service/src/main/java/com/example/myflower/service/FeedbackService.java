package com.example.myflower.service;

import com.example.myflower.dto.feedback.request.CreateFeedbackRequestDTO;
import com.example.myflower.dto.feedback.response.FeedbackResponseDTO;
import com.example.myflower.dto.feedback.response.RatingFeedbackResponseDTO;

import java.util.List;

public interface FeedbackService {

    FeedbackResponseDTO addFeedback(CreateFeedbackRequestDTO createFeedbackRequestDTO);

    List<FeedbackResponseDTO> getFeedbackByFlowerId(Integer flowerId);

    List<FeedbackResponseDTO> getFeedbackBySeller(Integer sellerId);

    void deleteFeedback(Integer id);

    void restoreFeedback(Integer id);

    RatingFeedbackResponseDTO ratingByUserId(Integer accountId);
}
