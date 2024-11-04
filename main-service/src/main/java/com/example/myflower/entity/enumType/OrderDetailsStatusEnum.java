package com.example.myflower.entity.enumType;

public enum OrderDetailsStatusEnum {
    PENDING,                    // 0
    PREPARING,                  // 1
    PROCESSING,                 // 2
    SHIPPED,                    // 3
    DELIVERED,                  // 5
    BUYER_CANCELED,             // 6
    SELLER_CANCELED,            // 7
    REFUNDED,                   // 8
    RETURN_REQUESTED,           // 9
    RETURNED,                   // 10
    CANCELLED(-1),              // 11 Hủy đơn hàng
    NOT_RECEIVED(1),            // 12 Chưa tiếp nhận
    RECEIVED(2),                // 13 Đã tiếp nhận
    PICKED_UP(3),               // 14 Đã lấy hàng/Đã nhập kho
    COORDINATING_DELIVERY(4),   // 15 Đã điều phối giao hàng/Đang giao hàng
    DELIVERED_UNVERIFIED(5),    // 16 Đã giao hàng/Chưa đối soát
    VERIFIED(6),                 // 17 Đã đối soát
    UNABLE_TO_PICK_UP(7),       // 18 Không lấy được hàng
    PICK_UP_DELAYED(8),         // 19 Hoãn lấy hàng
    UNABLE_TO_DELIVER(9),       // 20 Không giao được hàng
    DELIVERY_DELAYED(10),       // 21 Delay giao hàng
    RETURN_DEBT_VERIFIED(11),   // 22 Đã đối soát công nợ trả hàng
    COORDINATING_PICK_UP(12),   // 23 Đã điều phối lấy hàng/Đang lấy hàng
    COMPENSATION_ORDER(13),      // 24 Đơn hàng bồi hoàn
    RETURNING(20),               // 25 Đang trả hàng (COD cầm hàng đi trả)
    RETURNED_SUCCESSFULLY(21),   // 26 Đã trả hàng (COD đã trả xong hàng)
    SHIPPER_PICKED_UP(123),      // 27 Shipper báo đã lấy hàng
    SHIPPER_UNABLE_TO_PICK_UP(127), // 28 Shipper (nhân viên lấy/giao hàng) báo không lấy được hàng
    SHIPPER_PICK_UP_DELAYED(128), // 29 Shipper báo delay lấy hàng
    SHIPPER_DELIVERED(45),       // 30 Shipper báo đã giao hàng
    SHIPPER_UNABLE_TO_DELIVER(49), // 31 Shipper báo không giao được giao hàng
    SHIPPER_DELIVERY_DELAYED(410) // 32 Shipper báo delay giao hàng
    ;

    private final int code;

    OrderDetailsStatusEnum() {
        this.code = ordinal();
    }

    OrderDetailsStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
