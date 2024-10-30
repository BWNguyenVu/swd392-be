package com.example.myflower.dto.account.requests;

import com.example.myflower.entity.enumType.AccountRoleEnum;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsersRequestDTO {
    private List<AccountRoleEnum> roles;
    private String search;
    private String sortBy;
    private String order;
    private Integer pageNumber;
    private Integer pageSize;
}
