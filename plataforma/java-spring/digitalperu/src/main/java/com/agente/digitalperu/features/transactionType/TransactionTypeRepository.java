package com.agente.digitalperu.features.transactionType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long>{
    Optional<TransactionType> findByName(String name);
}
