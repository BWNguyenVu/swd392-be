package com.swd.notification_service.dto.flowers;

import com.swd.notification_service.dto.account.AccountResponseDTO;
import com.swd.notification_service.dto.flowers.EnumType.FlowerListingStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerListingResponseDTO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;
    private boolean isDeleted;
    private String rejectReason;
}
