package com.example.myflower.controller;

import com.example.myflower.dto.feedback.request.CreateFeedbackRequestDTO;
import com.example.myflower.dto.feedback.response.FeedbackResponseDTO;
import com.example.myflower.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
@CrossOrigin("**")
@RequiredArgsConstructor
public class FeedbackController {
    @NonNull
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> addFeedback(@Valid @RequestBody CreateFeedbackRequestDTO requestDTO) {
        return ResponseEntity.ok().body(feedbackService.addFeedback(requestDTO));
    }

    @GetMapping("/flower/{flowerId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackByFlowerId(@PathVariable Integer flowerId) {
        return ResponseEntity.ok().body(feedbackService.getFeedbackByFlowerId(flowerId));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackBySellerId(@PathVariable Integer sellerId) {
        return ResponseEntity.ok().body(feedbackService.getFeedbackBySeller(sellerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreFeedback(@PathVariable Integer id) {
        feedbackService.restoreFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
