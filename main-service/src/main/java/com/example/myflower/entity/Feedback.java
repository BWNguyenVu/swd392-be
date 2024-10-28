package com.example.myflower.entity;

import com.example.myflower.entity.enumType.RatingEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Table(indexes = {
        @Index(columnList = "flowerId"),
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = NOT_AUDITED)
public class Feedback {
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
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RatingEnum rating;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;
}
