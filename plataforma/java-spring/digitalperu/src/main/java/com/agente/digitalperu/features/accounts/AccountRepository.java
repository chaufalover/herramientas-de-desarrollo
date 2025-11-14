package com.agente.digitalperu.features.accounts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{

    Optional<Account> findByAccountNumber(String accountNumber);
    
}
