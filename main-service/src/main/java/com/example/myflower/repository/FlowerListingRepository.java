package com.example.myflower.repository;

import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import jakarta.persistence.LockModeType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlowerListingRepository extends JpaRepository<FlowerListing, Integer> {
    @Query("SELECT fl FROM FlowerListing fl " +
            "JOIN FETCH fl.user " +
            "LEFT JOIN FETCH fl.categories fc " +
            "WHERE :name IS NULL OR fl.name ILIKE %:name% " +
            "AND (:status IS NULL OR fl.status = :status) " +
            "AND (:minPrice IS NULL OR fl.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR fl.price <= :maxPrice) " +
            "AND (:isDeleted IS NULL OR fl.isDeleted = :isDeleted) " +
            "AND (:categoryIds IS NULL OR fl.id IN (" +
                "SELECT DISTINCT fl.id FROM FlowerListing fl " +
                "JOIN fl.categories fc WHERE fc.id IN :categoryIds AND fc.isDeleted = false" +
            ")) " +
            "AND (fc.isDeleted IS NULL OR fc.isDeleted = false)"
    )
    Page<FlowerListing> findAllByParameters(@Param("name") String name,
                                            @Param("categoryIds") List<Integer> categoryIds,
                                            @Param("status") FlowerListingStatusEnum status,
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice,
                                            @Param("isDeleted") Boolean isDeleted,
                                            Pageable pageable);

    @Query("SELECT fl FROM FlowerListing fl " +
            "JOIN FETCH fl.user " +
            "LEFT JOIN FETCH fl.categories fc " +
            "WHERE fl.id = :id " +
            "AND (:isDeleted IS NULL OR fl.isDeleted = :isDeleted) " +
            "AND (fc.isDeleted IS NULL OR fc.isDeleted = false)"
    )
    Optional<FlowerListing> findByIdAndDeleteStatus(@NotNull Integer id, Boolean isDeleted);

    @Query("SELECT fl FROM FlowerListing fl " +
            "JOIN FETCH fl.user u " +
            "LEFT JOIN FETCH fl.categories fc " +
            "WHERE u.id = :userId " +
            "AND (fc.isDeleted IS NULL OR fc.isDeleted = false)")
    List<FlowerListing> findByUserId(@Param("userId") Integer userId);

    Integer countFlowerListingByUserIdAndStatusNotIn(Integer userId, List<FlowerListingStatusEnum> statusList);

    List<FlowerListing> findByExpireDateBefore(LocalDateTime date);
    List<FlowerListing> findByExpireDateAfter(LocalDateTime date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FlowerListing f WHERE f.id = :id")
    FlowerListing findByIdWithLock(Integer id);

    @NotNull List<FlowerListing> findAll();

    List<FlowerListing> findAllByStockQuantity(Integer stockQuantity);
}
