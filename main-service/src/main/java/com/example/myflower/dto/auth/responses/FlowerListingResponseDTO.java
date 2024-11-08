package com.example.myflower.dto.auth.responses;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.file.FileResponseDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlowerListingResponseDTO implements Serializable {
    private Integer id;
    private AccountResponseDTO user;
    private String name;
    private String description;
    private BigDecimal price;
    private String eventType;
    private Integer stockQuantity;
    private String address;
    private List<FlowerCategoryResponseDTO> categories;
    private List<FileResponseDTO> images;
    private FlowerListingStatusEnum status;
    private LocalDateTime expireDate;
    private LocalDateTime flowerExpireDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;
    private Boolean isDeleted;
    private String rejectReason;
}
