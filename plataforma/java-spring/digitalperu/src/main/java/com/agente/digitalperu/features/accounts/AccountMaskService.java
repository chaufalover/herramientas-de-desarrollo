package com.agente.digitalperu.features.accounts;

import org.springframework.stereotype.Service;

@Service("accountMaskService")
public class AccountMaskService {

    public String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        
        // Obtener los últimos 4 dígitos
        String last4 = accountNumber.substring(accountNumber.length() - 4);
        
        // Calcular cuántos grupos de asteriscos necesitamos
        int maskedLength = accountNumber.length() - 4;
        int groups = (maskedLength + 3) / 4; // Redondear hacia arriba
        
        StringBuilder masked = new StringBuilder();
        
        // Agregar grupos de asteriscos
        for (int i = 0; i < groups; i++) {
            masked.append("**** ");
        }
        
        // Agregar los últimos 4 dígitos
        masked.append(last4);
        
        return masked.toString();
    }
    
    /**
     * Enmascara el número de cuenta con formato personalizado
     * Ejemplo: "1234567890123456" -> "1234 **** **** 3456"
     */
    public String maskAccountNumberPartial(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 8) {
            return "****";
        }
        
        String first4 = accountNumber.substring(0, 4);
        String last4 = accountNumber.substring(accountNumber.length() - 4);
        
        int middleGroups = (accountNumber.length() - 8 + 3) / 4;
        StringBuilder middle = new StringBuilder();
        
        for (int i = 0; i < middleGroups; i++) {
            middle.append("**** ");
        }
        
        return first4 + " " + middle + last4;
    }
    
    /**
     * Enmascara email
     * Ejemplo: "juan@email.com" -> "j***n@email.com"
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        
        if (local.length() <= 2) {
            return local.charAt(0) + "***@" + domain;
        }
        
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }
}