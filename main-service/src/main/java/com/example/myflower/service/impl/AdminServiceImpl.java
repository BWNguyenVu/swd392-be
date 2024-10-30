package com.example.myflower.service.impl;

import com.example.myflower.dto.admin.requests.CreateAccountIntegrationRequest;
import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.FlowerListing;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import com.example.myflower.entity.enumType.FlowerListingStatusEnum;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.mapper.FlowerListingMapper;
import com.example.myflower.repository.AccountRepository;
import com.example.myflower.repository.FlowerListingRepository;
import com.example.myflower.service.AdminService;
import com.example.myflower.utils.AccountUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaTemplate<String, CreateAccountIntegrationRequest> kafkaTemplate;

    @Override
    public Account getAccountAdmin() {
        List<Account> accounts = accountRepository.findAccountsByRole(AccountRoleEnum.ADMIN);
        return accounts.get(0);
    }

    @Override
    public void createAccountIntegration(CreateAccountIntegrationRequest requestDTO){
        kafkaTemplate.send("create_account-integration_topic", requestDTO);
    }

}
