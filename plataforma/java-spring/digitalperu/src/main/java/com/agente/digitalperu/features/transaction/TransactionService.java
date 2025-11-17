package com.agente.digitalperu.features.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.agente.digitalperu.features.accounts.Account;
import com.agente.digitalperu.features.accounts.AccountRepository;
import com.agente.digitalperu.util.TransactionEnum;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction transfer(String originNumber, String destinationNumber, BigDecimal amount) {

       
        Account origin = accountRepository.findByAccountNumber(originNumber)
                .orElseThrow(() -> new RuntimeException("Origin account not found"));

        Account destination = accountRepository.findByAccountNumber(destinationNumber)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        
        if (origin.getId().equals(destination.getId())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        if (origin.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        origin.setBalance(origin.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(origin);
        accountRepository.save(destination);

        Transaction tx = new Transaction();
        tx.setOriginAccount(origin);
        tx.setDestinationAccount(destination);
        tx.setAmount(amount);
        tx.setTransactionType(TransactionEnum.TRANSFER);
        tx.setTransactionDate(LocalDate.now());

        return transactionRepository.save(tx);
    }
}
