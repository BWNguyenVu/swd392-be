package com.example.myflower.dto.feedback.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackRequestDTO {
    @NotNull(message = "Flower ID is required")
    private Integer flowerId;
    @NotBlank(message = "Name of the flower is required")
    @Size(max = 1000, message = "Feedback content must not exceed 1000 characters long")
    private String description;
    @NotNull(message = "Rating value is required")
    @Min(value = 1, message = "Rating values must be from 1-5")
    @Max(value = 5,  message = "Rating values must be from 1-5")
    private Integer rating;
}
