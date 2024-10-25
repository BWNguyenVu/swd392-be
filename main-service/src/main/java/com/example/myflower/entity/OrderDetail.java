package com.example.myflower.entity;

import com.example.myflower.entity.enumType.OrderDetailsStatusEnum;
import com.example.myflower.entity.enumType.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderSummaryId",referencedColumnName = "id", nullable = false)
    private OrderSummary orderSummary;
    @ManyToOne()
    @JoinColumn(name = "sellerId",referencedColumnName = "id", nullable = false)
    private Account seller;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flowerId", referencedColumnName = "id", nullable = false)
    private FlowerListing flowerListing;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderDetailsStatusEnum status;
    @Column(nullable = true)
    private String cancelReason;
    @Column(nullable = true)
    private boolean isRefund;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
