package com.example.myflower.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private Account user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flowerId", referencedColumnName = "id", nullable = false)
    private FlowerListing flower;
    @Column(nullable = false)
    private Integer quantity;
}
