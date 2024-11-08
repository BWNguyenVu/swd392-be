package com.example.myflower.service;

import com.example.myflower.dto.address.requests.AddressRequestDTO;
import com.example.myflower.dto.address.responses.AddressResponseDTO;

import java.util.List;

public interface AddressService {
    AddressResponseDTO createAddress(AddressRequestDTO requestDTO);

    AddressResponseDTO getAddressById(Integer id);

    List<AddressResponseDTO> getAllByAccount();

    AddressResponseDTO updateAddress(Integer id, AddressRequestDTO requestDTO);

    void deleteAddress(Integer id);
}
