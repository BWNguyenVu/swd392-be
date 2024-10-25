package com.example.myflower.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
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
    private MediaFile mediaFile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id")
    private FlowerListing flowerListing;
    @Column(nullable = true)
    private Integer sortOrder;
}
