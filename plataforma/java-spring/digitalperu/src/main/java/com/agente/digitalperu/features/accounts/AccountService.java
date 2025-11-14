package com.agente.digitalperu.features.accounts;

import java.util.List;
import org.springframework.stereotype.Service;
import com.agente.digitalperu.features.accountType.AccountTypeRepository;
import com.agente.digitalperu.features.customers.Customer;
import com.agente.digitalperu.features.customers.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountTypeRepository accountTypeRepository;

    public List<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }
    @Transactional
    public Account updateAddAccount(Account account) {
        if (account.getCustomer() == null || account.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente antes de guardar la cuenta.");
        }

        var customer = customerRepository.findById(account.getCustomer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        account.setCustomer(customer);

        if (account.getType() == null || account.getType().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de cuenta válido.");
        }

        var type = accountTypeRepository.findById(account.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cuenta no encontrado"));
        account.setType(type);

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    //inicio de sesion con qr n contraseña:b
    public Customer getAccountNumber(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getCustomer)
            .orElse(null);
    }

}