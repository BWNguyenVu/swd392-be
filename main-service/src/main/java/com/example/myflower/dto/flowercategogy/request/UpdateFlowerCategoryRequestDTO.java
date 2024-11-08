package com.example.myflower.dto.flowercategogy.request;

import com.example.myflower.entity.enumType.CategoryParentEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFlowerCategoryRequestDTO {
    @NotBlank(message = "Name of the flower category is required")
    @Size(max = 100, message = "Flower name must not exceed 255 characters long")
    private String name;
    @NotNull(message = "Parent category is required")
    private CategoryParentEnum parentCategory;
    private MultipartFile image;
}
