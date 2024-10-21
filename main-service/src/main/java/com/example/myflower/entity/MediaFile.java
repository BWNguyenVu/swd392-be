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
    public Integer id;
    @Column(nullable = false)
    public String url;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public StorageMethodEnum storageMethod;
    @Column(nullable = false)
    public LocalDateTime createdAt;
}
