package com.example.myflower.consumers;

import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountConsumer {
    @NonNull
    private final AccountRepository userRepository;
    @NonNull
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-request-topic", groupId = "user-result-group")
    @SendTo("user-result-topic")
    public String consumeUserRequest() {
        try {
            List<Account> users = userRepository.findAll();
            List<AccountResponseDTO> accountResponseDTOList = users.stream()
                    .map(account -> AccountResponseDTO.builder()
                            .id(account.getId())
                            .name(account.getName())
                            .phone(account.getPhone())
                            .email(account.getEmail())
                            .build())
                    .toList();
            return objectMapper.writeValueAsString(accountResponseDTOList);
        }
        catch (Exception e) {
            return null;
        }
    }
}
