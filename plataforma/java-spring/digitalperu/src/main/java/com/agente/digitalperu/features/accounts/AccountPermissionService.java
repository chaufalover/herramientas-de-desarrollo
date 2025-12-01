package com.agente.digitalperu.features.accounts;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("accountPermissions")
@Slf4j
public class AccountPermissionService {
    
    public boolean canDeposit(String accountType) {
        if (accountType == null) {
            return false;
        }
        
        String type = accountType.toUpperCase();
        boolean can = type.contains("DEBITO") || 
                      type.contains("AHORRO");
        
        log.debug("canDeposit({}) = {}", accountType, can);
        return can;
    }

    public boolean canWithdraw(String accountType) {
        return accountType != null;
    }


    public boolean canTransfer(String accountType) {
        if (accountType == null) {
            return false;
        }
        String type = accountType.toUpperCase();
        boolean can = type.contains("DEBITO") || 
                      type.contains("AHORRO");
        
        log.debug("canTransfer({}) = {}", accountType, can);
        return can;
    }

    public boolean canViewBalance(String accountType) {

        return accountType != null;
    }

    public String getAccountTypeMessage(String accountType) {
        if (accountType == null) {
            return "Tipo de cuenta no disponible";
        }
        
        String type = accountType.toUpperCase();
        
        if (type.contains("CREDITO")) {
            return "Cuenta de Crédito - Solo puede realizar pagos (retiros) y consultar su saldo";
        } else if (type.contains("DEBITO")) {
            return "Cuenta de Débito - Todas las operaciones disponibles";
        } else {
            return "Cuenta: " + accountType;
        }
    }

}
