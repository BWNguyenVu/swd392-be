package com.example.myflower.entity;

import com.example.myflower.entity.enumType.PaymentMethodEnum;
import com.example.myflower.entity.enumType.WalletLogActorEnum;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Audited
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Account user;
    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletLogTypeEnum type;
    @Enumerated(EnumType.STRING)
    private WalletLogActorEnum actorEnum;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = true)
    private BigDecimal balance;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletLogStatusEnum status;
    @Column(nullable = false)
    private boolean isClosed;
    @Column(nullable = true)
    private boolean isRefund;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
    }

}
