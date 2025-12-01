package com.agente.digitalperu.features.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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
    public BigDecimal transfer(
            String originAccountNumber,
            String destinationAccountNumber,
            BigDecimal amount,
            Long customerId) {

        if (originAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("No puedes transferir a la misma cuenta.");
        }

        Account origin = accountRepository.findByAccountNumber(originAccountNumber)
                .orElseThrow(() -> new NoSuchElementException("La cuenta de origen no existe."));

        if (!origin.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("No puedes transferir desde una cuenta que no es tuya.");
        }

        Account destination = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> new NoSuchElementException("La cuenta de destino no existe."));

        if (!destination.getType().getName().equalsIgnoreCase("DEBITO")) {
            throw new IllegalArgumentException("La cuenta destino debe ser de tipo DEBITO.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }

        if (origin.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        origin.setBalance(origin.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(origin);
        accountRepository.save(destination);

        Transaction transaction = Transaction.builder()
                .originAccount(origin)
                .destinationAccount(destination)
                .amount(amount)
                .transactionType(TransactionEnum.TRANSFER)
                .transactionDate(LocalDate.now())
                .build();

        transactionRepository.save(transaction);

        return origin.getBalance();
    }

    public List<TransactionHistoryDTO> getHistory(String accountNumber) {

    List<Transaction> trans = transactionRepository.findByAccountHistory(accountNumber);

    return trans.stream().map(t -> {

        boolean incoming = t.getDestinationAccount().getAccountNumber().equals(accountNumber);

        return new TransactionHistoryDTO(
                t.getAmount(),
                t.getTransactionDate(),
                t.getTransactionType().name(),
                incoming
        );
    }).toList();
}


}
