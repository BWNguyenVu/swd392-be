package com.example.myflower.dto.feedback.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingFeedbackResponseDTO {
    private Double average;
    private Integer countFeedback;
}
