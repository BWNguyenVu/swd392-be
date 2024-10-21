package com.example.myflower.repository;

import com.example.myflower.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Integer> {
}
