package com.example.myflower.repository;

import com.example.myflower.entity.FlowerListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FlowerListingRepository extends JpaRepository<FlowerListing, Integer> {
    Page<FlowerListing> findByNameContainingAndIsDeletedFalse(String name, Pageable pageable);
    Page<FlowerListing> findByIsDeletedFalse(Pageable pageable);
}
