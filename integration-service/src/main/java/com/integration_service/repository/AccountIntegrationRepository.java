package com.integration_service.repository;

import com.integration_service.entity.AccountIntegration;
import com.integration_service.entity.enumType.AccountProviderEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountIntegrationRepository extends JpaRepository<AccountIntegration, Integer> {
    AccountIntegration findAccountIntegrationByProvider(AccountProviderEnum providerEnum);
}
