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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlowerListingRequestDTO {
    @NotBlank(message = "Name of the flower is required")
    @Size(min = 5, message = "Flower name must be at least 5 characters long")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    private BigDecimal price;
    @NotNull(message = "Stock balance is required")
    @Min(value = 0, message = "Stock balance must not be negative number")
    private Integer stockBalance;
    @NotBlank(message = "Address is required")
    private String address;
    private List<Integer> categories;
    @NotNull(message = "Flower image is required")
    private MultipartFile image;
}
