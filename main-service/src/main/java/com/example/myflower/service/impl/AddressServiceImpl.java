package com.example.myflower.service.impl;

import com.example.myflower.dto.address.requests.AddressRequestDTO;
import com.example.myflower.dto.address.responses.AddressResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.Address;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.order.OrderAppException;
import com.example.myflower.mapper.AddressMapper;
import com.example.myflower.repository.AddressRepository;
import com.example.myflower.service.AccountService;
import com.example.myflower.service.AddressService;
import com.example.myflower.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO requestDTO) {
        Account account = AccountUtils.getCurrentAccount();
        Address address = Address.builder()
                .recipientName(requestDTO.getRecipientName())
                .streetAddress(requestDTO.getStreetAddress())
                .city(requestDTO.getCity())
                .district(requestDTO.getDistrict())
                .province(requestDTO.getProvince())
                .phoneNumber(requestDTO.getPhoneNumber())
                .user(account)
                .createAt(LocalDateTime.now())
                .build();

        Address savedAddress = addressRepository.save(address);
        return addressMapper.convertToResponseDTO(savedAddress);
    }

    @Override
    public AddressResponseDTO getAddressById(Integer id) {
        Account account = AccountUtils.getCurrentAccount();
        Address address = addressRepository.findByIdAndUser_Id(id, account.getId());
        if (address == null) {
            throw new RuntimeException("Address not found");
        }
        return addressMapper.convertToResponseDTO(address);
    }

    @Override
    public List<AddressResponseDTO> getAllByAccount() {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || account.getBalance() == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        List<Address> addresses = addressRepository.findAllByUser_Id(account.getId());
        if (addresses.isEmpty()) {
            throw new RuntimeException("Addresses not found");
        }
        return  addresses.stream()
                .map(addressMapper::convertToResponseDTO)
                .toList();
    }

    @Override
    public AddressResponseDTO updateAddress(Integer id, AddressRequestDTO requestDTO) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || account.getBalance() == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Address address = addressRepository.findByIdAndUser_Id(id, account.getId());
        if (address == null) {
            throw new RuntimeException("Address not found");
        }
        address.setRecipientName(requestDTO.getRecipientName());
        address.setStreetAddress(requestDTO.getStreetAddress());
        address.setCity(requestDTO.getCity());
        address.setDistrict(requestDTO.getDistrict());
        address.setProvince(requestDTO.getProvince());
        address.setPhoneNumber(requestDTO.getPhoneNumber());
        address.setUpdateAt(LocalDateTime.now());
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.convertToResponseDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Integer id) {
        Account account = AccountUtils.getCurrentAccount();
        if (account == null || account.getBalance() == null) {
            throw new OrderAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Address address = addressRepository.findByIdAndUser_Id(id, account.getId());
        if (address == null) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.delete(address);
    }
}
