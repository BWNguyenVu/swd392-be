package com.example.myflower.repository;

import com.example.myflower.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Address findByIdAndUser_Id(Integer id, Integer accountId);
    List<Address> findAllByUser_Id(Integer accountId);
}
