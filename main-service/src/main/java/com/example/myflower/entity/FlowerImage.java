package com.example.myflower.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(indexes = {
        @Index(columnList = "flower_id"),
        @Index(columnList = "media_file_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "media_file_id")
    @Fetch(FetchMode.JOIN)
    private MediaFile mediaFile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id")
    private FlowerListing flowerListing;
    @Column(nullable = true)
    private Integer sortOrder;
}
