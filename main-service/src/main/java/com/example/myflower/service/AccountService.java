package com.example.myflower.service;

import com.example.myflower.dto.account.requests.AddBalanceRequestDTO;
import com.example.myflower.dto.account.requests.UpdateAccountRequestDTO;
import com.example.myflower.dto.account.requests.UploadFileRequestDTO;
import com.example.myflower.dto.account.responses.AccountResponseDTO;
import com.example.myflower.dto.account.responses.AddBalanceResponseDTO;
import com.example.myflower.dto.account.responses.GetBalanceResponseDTO;
import com.example.myflower.entity.Account;
import com.example.myflower.entity.OrderSummary;
import com.example.myflower.entity.Payment;
import com.example.myflower.entity.enumType.WalletLogActorEnum;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<AddBalanceResponseDTO> addBalance(AddBalanceRequestDTO addBalanceRequestDTO);
    ResponseEntity<GetBalanceResponseDTO> getBalance();
    Account handleBalanceByOrder(Account account, BigDecimal amount, WalletLogTypeEnum type, WalletLogActorEnum actorEnum, OrderSummary orderSummary, Payment payment, WalletLogStatusEnum status);
    AccountResponseDTO getProfile();
    AccountResponseDTO uploadAvatar(UploadFileRequestDTO uploadFileRequestDTO) throws IOException;
    AccountResponseDTO updateProfile(UpdateAccountRequestDTO accountRequestDTO);
}
