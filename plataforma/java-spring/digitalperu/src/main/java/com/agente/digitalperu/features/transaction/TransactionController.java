package com.agente.digitalperu.features.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.agente.digitalperu.features.accounts.Account;
import com.agente.digitalperu.features.accounts.AccountService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
     private final AccountService accountService;

    @GetMapping()
     public String operationsPage(Model model, HttpSession session) {
         Long customerId = (Long) session.getAttribute("customerId");
         if (customerId == null) {
             return "redirect:/login";
         }

         List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
         java.math.BigDecimal total = accounts.stream()
                 .map(Account::getBalance)
                 .filter(b -> b != null)
                 .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

         model.addAttribute("accounts", accounts);
         model.addAttribute("totalBalance", total);
         return "user/operaciones";
     }

   @GetMapping("/deposit")
    public String depositsPage(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/login"; 
        }

        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        model.addAttribute("accounts", accounts);
        return "user/depositos";
    }
    
   @PostMapping("/deposit")
    @ResponseBody
    public ResponseEntity<?> deposit(@RequestBody Map<String, String> payload, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        String numeroCuenta = payload.get("numeroCuenta");
        String montoStr = payload.get("monto");
        if (numeroCuenta == null || montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos requeridos: numeroCuenta, monto"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.deposit(numeroCuenta, monto, customerId);
            return ResponseEntity.ok(Map.of("mensaje", "Depósito exitoso", "nuevoSaldo", nuevoSaldo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno"));
        }
    }

    @GetMapping("/retirement")
    public String retirement(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/login"; 
        }

        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        model.addAttribute("accounts", accounts);
        return "user/retiros";
    }

    @PostMapping("/retirement")
    @ResponseBody
    public ResponseEntity<?> withdraw(@RequestBody Map<String, String> payload, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        String numeroCuenta = payload.get("numeroCuenta");
        String montoStr = payload.get("monto");
        if (numeroCuenta == null || montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos requeridos: numeroCuenta, monto"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.retirement(numeroCuenta, monto, customerId);
            return ResponseEntity.ok(Map.of("mensaje", "Retiro exitoso", "nuevoSaldo", nuevoSaldo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno"));
        }
    }

}
