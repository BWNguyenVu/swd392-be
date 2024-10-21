package com.example.myflower.dto.order.requests;

import com.example.myflower.dto.BasePaginationRequestDTO;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
public class GetOrderByAccountRequestDTO extends BasePaginationRequestDTO {
    private List<OrderDetailsStatusEnum> status;
}
