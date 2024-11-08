package com.example.myflower.entity;

import com.example.myflower.entity.enumType.StorageMethodEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 1000)
    private String fileName;
    @Column(nullable = true, length = 1000)
    private String caption;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageMethodEnum storageMethod;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
    }
}
