package com.example.myflower.dto.order.requests;

import com.example.myflower.dto.BasePaginationRequestDTO;
import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderDetailsBySellerRequestDTO extends BasePaginationRequestDTO {
    private List<OrderDetailsStatusEnum> status;
    private String search;
}
