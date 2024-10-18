package com.example.myflower.entity;

import com.example.myflower.entity.enumType.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class OrderSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private Account user;
    @OneToOne(fetch = FetchType.LAZY)
    private Transaction transaction;
    @Column(nullable = false, length = 100)
    private String buyerName;
    @Column(nullable = false, length = 50)
    private String buyerPhone;
    @Column(nullable = false, length = 100)
    private String buyerEmail;
    @Column(nullable = false)
    private String buyerAddress;
    @Column(nullable = false)
    private String note;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private OrderStatusEnum status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
