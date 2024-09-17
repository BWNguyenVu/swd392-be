package com.example.myflower.entity.enumType;

import lombok.Getter;

@Getter
public enum RatingEnum {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final Integer value;

    RatingEnum(Integer value) {
        this.value = value;
    }
}
