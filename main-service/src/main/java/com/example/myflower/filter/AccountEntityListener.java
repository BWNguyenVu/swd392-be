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
        if (account.getAvatar() != null) {
            String fileUrl = storageService.getFileUrl(account.getAvatar());
            account.setAvatar(fileUrl);
        }
    }
}
