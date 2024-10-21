package com.swd.notification_service.entity.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum NotificationTypeEnum {
    WELCOME("Welcome"),
    FLOWER_LISTING_STATUS("FlowerListingStatus"),
    ORDER_STATUS("OrderStatus"),
    MARKETING("Marketing"),
    ;
    private String value;
}
