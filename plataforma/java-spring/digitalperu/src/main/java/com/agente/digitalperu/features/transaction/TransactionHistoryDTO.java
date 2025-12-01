package com.agente.digitalperu.features.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionHistoryDTO {
     private BigDecimal amount;
    private LocalDate date;
    private String type;
    private boolean isIncoming;
}
