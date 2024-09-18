package com.example.myflower.entity;

import com.example.myflower.entity.enumType.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private Account user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flowerId", referencedColumnName = "id", nullable = false)
    private FlowerListing flowerListing;
    @Column(nullable = false, length = 100)
    private String buyerName;
    @Column(nullable = false, length = 50)
    private String buyerPhone;
    @Column(nullable = false, length = 100)
    private String buyerEmail;
    @Column(nullable = false)
    private String buyerAddress;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
}
