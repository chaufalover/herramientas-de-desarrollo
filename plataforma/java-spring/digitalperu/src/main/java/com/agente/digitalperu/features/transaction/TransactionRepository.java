package com.agente.digitalperu.features.transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.originAccount.accountNumber = :accountNumber
           OR t.destinationAccount.accountNumber = :accountNumber
        ORDER BY t.transactionDate DESC
    """)
    List<Transaction> findByAccountHistory(@Param("accountNumber") String accountNumber);

}
