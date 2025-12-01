package com.agente.digitalperu.features.accounts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agente.digitalperu.features.accountType.AccountTypeRepository;
import com.agente.digitalperu.features.customers.Customer;
import com.agente.digitalperu.features.customers.CustomerRepository;
import com.agente.digitalperu.features.transaction.Transaction;
import com.agente.digitalperu.features.transaction.TransactionRepository;
import com.agente.digitalperu.util.TransactionEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final AccountTypeRepository accountTypeRepository;

    public List<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    public List<Account> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
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

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Transactional
    public BigDecimal deposit(String accountNumberDest, BigDecimal amount, Long customerId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }

        var dest = accountRepository.findByAccountNumber(accountNumberDest)
                .orElseThrow(() -> new NoSuchElementException("Cuenta destino no encontrada"));


        if (!dest.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("No autorizado para operar sobre esta cuenta");
        }

        final String SYSTEM_ACCOUNT = "SYSTEM-0000";
        var origin = accountRepository.findByAccountNumber(SYSTEM_ACCOUNT)
                .orElseGet(() -> {
                    Account a = Account.builder()
                            .accountNumber(SYSTEM_ACCOUNT)
                            .balance(BigDecimal.ZERO)

                            .openingDate(LocalDate.now())

                            .customer(null)
                            .build();
                     return accountRepository.save(a);
                });

        dest.setBalance(dest.getBalance().add(amount));
        accountRepository.save(dest);


        Transaction tx = Transaction.builder()
                .originAccount(origin)
                .destinationAccount(dest)
                .transactionType(TransactionEnum.DEPOSIT)
                .amount(amount)
                .transactionDate(LocalDate.now())
                .build();
        transactionRepository.save(tx);

        return dest.getBalance();
    }

    @Transactional
    public BigDecimal retirement(String accountNumberOrigin, BigDecimal amount, Long customerId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }

        var origin = accountRepository.findByAccountNumberForUpdate(accountNumberOrigin)
                .orElseThrow(() -> new NoSuchElementException("Cuenta origen no encontrada"));

  
        if (!origin.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("No autorizado para operar sobre esta cuenta");
        }

 
        if (origin.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

     
        origin.setBalance(origin.getBalance().subtract(amount));
        accountRepository.save(origin);

        final String SYSTEM_ACCOUNT = "SYSTEM-0000";
        var dest = accountRepository.findByAccountNumber(SYSTEM_ACCOUNT)
                .orElseGet(() -> {
                    Account a = Account.builder()
                            .accountNumber(SYSTEM_ACCOUNT)
                            .balance(BigDecimal.ZERO)
                            .openingDate(LocalDate.now()) 
                            .customer(null)
                            .build();
                    return accountRepository.save(a);
                });

        Transaction tx = Transaction.builder()
                .originAccount(origin)
                .destinationAccount(dest)
                .transactionType(TransactionEnum.RETIREMENT) 
                .amount(amount)
                .transactionDate(LocalDate.now())
                .build();
        transactionRepository.save(tx);

        return origin.getBalance();
    }

}