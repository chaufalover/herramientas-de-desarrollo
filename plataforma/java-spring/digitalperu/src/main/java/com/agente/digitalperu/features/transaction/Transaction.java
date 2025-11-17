package com.agente.digitalperu.features.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.agente.digitalperu.features.accounts.Account;
import com.agente.digitalperu.util.TransactionEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_account_origin", nullable = false)
    private Account originAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_account_destination", nullable = false)
    private Account destinationAccount;

    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
}
