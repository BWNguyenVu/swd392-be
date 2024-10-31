package com.example.myflower.dto.order.responses;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountOrderStatusResponseDTO {
    private int pendingCount;
    private int preparingCount;
    private int shippedCount;
    private int deliveredCount;
    private int buyerCancelledCount;
    private int sellerCancelledCount;
    private int refundedCount;
}
