package com.example.myflower.repository;

import com.example.myflower.entity.FlowerListing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowerListingRepository extends CrudRepository<FlowerListing, Integer> {
}
