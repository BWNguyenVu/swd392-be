package com.example.myflower.repository;

import com.example.myflower.entity.FlowerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FlowerCategoryRepository extends JpaRepository<FlowerCategory, Integer> {
    @Query("SELECT fc FROM FlowerCategory fc WHERE :isDeleted IS NULL OR fc.isDeleted = :isDeleted")
    List<FlowerCategory> findAllByDeleteStatus(Boolean isDeleted);

    @Query("SELECT fc FROM FlowerCategory fc " +
            "WHERE (:isDeleted IS NULL OR fc.isDeleted = :isDeleted) " +
            "AND fc.id = :id")
    Optional<FlowerCategory> findByIdAndDeleteStatus(Integer id, Boolean isDeleted);

    List<FlowerCategory> findByIdIn(List<Integer> ids);
}
