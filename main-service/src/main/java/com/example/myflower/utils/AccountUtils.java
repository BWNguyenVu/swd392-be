package com.example.myflower.utils;

import com.example.myflower.entity.Account;
import com.example.myflower.entity.enumType.AccountRoleEnum;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {
    public static Account getCurrentAccount(){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (object instanceof Account) {
            return (Account) object;
        } else {
            return null;
        }
    }
    public static boolean isAdminRole(Account account) {
        return account != null && account.getRole().equals(AccountRoleEnum.ADMIN);
    }
}
