package com.example.myflower.entity.enumType;

import com.fasterxml.jackson.annotation.JsonValue;
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

    public static RatingEnum valueOf(Integer value) {
        for (RatingEnum rating : values()) {
            if (rating.getValue().equals(value)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid rating value: " + value);
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }
}
