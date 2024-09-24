package com.example.myflower.entity;

import com.example.myflower.entity.enumType.RatingEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private Account user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flowerId", referencedColumnName = "id", nullable = false)
    private FlowerListing flowerListing;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RatingEnum rating;
}