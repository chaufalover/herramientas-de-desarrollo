package com.agente.digitalperu.features.accountType;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountTypeService {
    private final AccountTypeRepository accountTypeRepository;
     public List<AccountType> getAllAccountType(){
        return accountTypeRepository.findAll();
    }
}
