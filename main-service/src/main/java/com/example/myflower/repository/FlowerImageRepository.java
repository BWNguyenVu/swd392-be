package com.example.myflower.repository;

import com.example.myflower.entity.FlowerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowerImageRepository extends JpaRepository<FlowerImage, Integer> {
    List<FlowerImage> findAllByFlowerListingId(Integer flowerId);
    void deleteAllByMediaFileIdIn(List<Integer> mediaFileIds);
}
