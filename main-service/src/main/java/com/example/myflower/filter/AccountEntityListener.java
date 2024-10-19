package com.example.myflower.filter;

import com.example.myflower.entity.Account;
import com.example.myflower.service.StorageService;
import jakarta.persistence.PostLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountEntityListener {
    @Autowired
    private StorageService storageService;

    @PostLoad
    public void onPostLoad(Account account) {
        System.out.println(account.isSkipPostLoad());
        if (account.getAvatar() != null && !account.isSkipPostLoad()) {
            String fileUrl = storageService.getFileUrl(account.getAvatar());
            account.setAvatar(fileUrl);
        }
    }
}
