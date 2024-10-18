package com.example.myflower.dto.walletLog.requests;

import com.example.myflower.dto.BasePaginationRequestDTO;
import com.example.myflower.entity.enumType.WalletLogStatusEnum;
import com.example.myflower.entity.enumType.WalletLogTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
public class GetWalletLogsRequestDTO extends BasePaginationRequestDTO {
    private List<WalletLogStatusEnum> status;
    private List<WalletLogTypeEnum> type;
}
