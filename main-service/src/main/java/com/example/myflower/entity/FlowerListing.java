package com.example.myflower.entity;

import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private String flowerType;
    @Column(nullable = false)
    private String eventType;
    @Column(nullable = false)
    private Integer stockBalance;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FlowerListingStatusEnum status;
}
