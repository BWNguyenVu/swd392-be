package com.example.myflower.mapper;

import com.example.myflower.dto.address.responses.AddressResponseDTO;
import com.example.myflower.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    @Autowired
    private AccountMapper accountMapper;

    public AddressResponseDTO convertToResponseDTO(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .district(address.getDistrict())
                .province(address.getProvince())
                .phoneNumber(address.getPhoneNumber())
                .account(accountMapper.mapToAccountResponseDTO(address.getUser()))
                .createAt(address.getCreateAt())
                .updateAt(address.getUpdateAt())
                .build();
    }
}
