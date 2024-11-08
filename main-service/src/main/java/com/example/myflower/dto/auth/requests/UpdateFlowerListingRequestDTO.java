package com.example.myflower.dto.auth.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFlowerListingRequestDTO {
    @NotBlank(message = "Name of the flower is required")
    @Size(min = 5, message = "Flower name must be at least 5 characters long")
    private String name;
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Flower description cannot exceed 1000 characters long")
    private String description;
    @NotNull(message = "Price is required")
    private BigDecimal price;
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must not be negative number")
    private Integer stockQuantity;
    @NotBlank(message = "Address is required")
    private String address;
    @NotNull(message = "Expire date is required")
    private LocalDateTime expireDate;
    @NotNull(message = "Flower's expire date is required")
    private LocalDateTime flowerExpireDate;
    private List<Integer> categories;
    private List<MultipartFile> newImages;
    private List<Integer> deletedImages = new ArrayList<>();
}
