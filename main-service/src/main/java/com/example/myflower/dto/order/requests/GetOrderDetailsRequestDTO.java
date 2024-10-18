package com.example.myflower.dto.order.requests;

import com.example.myflower.dto.BasePaginationRequestDTO;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
public class GetOrderDetailsRequestDTO extends BasePaginationRequestDTO {
    private List<OrderDetailsStatusEnum> status;
    private String search;
}
