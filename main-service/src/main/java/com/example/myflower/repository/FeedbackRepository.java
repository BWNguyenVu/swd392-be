package com.example.myflower.repository;

import com.example.myflower.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findAllByFlowerIdAndIsDeletedFalse(Integer flowerId);
    List<Feedback> findAllByFlowerIdInAndIsDeletedFalse(List<Integer> flowerIds);
    List<Feedback> findAllByFlowerId(Integer flowerId);
    boolean existsByFlowerIdAndUserIdAndIsDeletedFalse(Integer flowerId, Integer userId);
    Optional<Feedback> findByIdAndIsDeletedFalse(Integer id);
}