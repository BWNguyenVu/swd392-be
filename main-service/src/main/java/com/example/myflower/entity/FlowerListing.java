package com.example.myflower.entity;

import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Audited
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Account user;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private int views;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = true)
    private String eventType;
    @ManyToMany
    @JoinTable(name = "flower_listing_categories", joinColumns = @JoinColumn(name = "flower_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<FlowerCategory> categories = new HashSet<>();
    @Column(nullable = false)
    private Integer stockQuantity;
    @Column(nullable = false)
    private String address;
    @OneToMany(mappedBy = "flowerListing", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<FlowerImage> images = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private FlowerListingStatusEnum status;
    @Column(name = "expire_date")
    private LocalDateTime expireDate;
    @Column(name = "flower_expire_date")
    private LocalDateTime flowerExpireDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Column(name ="reject_reason")
    private String rejectReason;
}
