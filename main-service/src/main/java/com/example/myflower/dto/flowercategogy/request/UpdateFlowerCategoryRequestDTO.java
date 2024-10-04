package com.example.myflower.dto.flowercategogy.request;

import com.example.myflower.entity.enumType.CategoryParentEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFlowerCategoryRequestDTO {
    private String name;
    private CategoryParentEnum parentCategory;
}
