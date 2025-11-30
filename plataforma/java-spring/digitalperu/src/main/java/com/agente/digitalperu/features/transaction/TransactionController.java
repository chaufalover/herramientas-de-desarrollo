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
import com.agente.digitalperu.features.customers.Customer;
import com.agente.digitalperu.features.customers.CustomerService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final AccountService accountService;
    private final CustomerService customerService;
    private final TransactionService transactionService;

    @GetMapping()
    public String operationsPage(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/login";
        }

        String accountType = (String) session.getAttribute("accountType");
        log.info("📊 Tipo de cuenta: {}", accountType);

        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        java.math.BigDecimal total = accounts.stream()
                .map(Account::getBalance)
                .filter(b -> b != null)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance", total);
        model.addAttribute("accountType", accountType);
        return "user/operaciones";
    }

    @GetMapping("/deposit")
    public String depositsPage(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber");

        if (customerId == null) {
            return "redirect:/login";
        }

        if (accountNumber == null || accountNumber.isEmpty()) {
            log.warn("⚠️ No hay número de cuenta en la sesión");
            model.addAttribute("error", "No se encontró tu cuenta");
            return "user/depositos";
        }

        log.info("🔍 Obteniendo cuenta del usuario: {}", accountNumber);
        Account account = accountService.getAccountByNumber(accountNumber);

        if (account == null) {
            log.warn("⚠️ La cuenta {} no existe en la BD", accountNumber);
            model.addAttribute("error", "La cuenta no existe");
            return "user/depositos";
        }

        model.addAttribute("account", account);
        return "user/depositos";
    }

    @PostMapping("/deposit")
    @ResponseBody
    public ResponseEntity<?> deposit(@RequestBody Map<String, String> payload, HttpSession session) {

        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber"); // 👈 SE USA LA CUENTA DE LA SESIÓN

        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        if (accountNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No hay número de cuenta en sesión"));
        }

        String montoStr = payload.get("monto");
        if (montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Debe ingresar monto"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.deposit(accountNumber, monto, customerId);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Depósito exitoso", "nuevoSaldo", nuevoSaldo));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno"));
        }
    }

    @GetMapping("/retirement")
    public String retirement(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber");

        if (customerId == null) {
            return "redirect:/login";
        }

        if (accountNumber == null || accountNumber.isEmpty()) {
            log.warn("⚠️ No hay número de cuenta en la sesión");
            model.addAttribute("error", "No se encontró tu cuenta");
            return "user/retiros";
        }

        log.info("🔍 Obteniendo cuenta del usuario: {}", accountNumber);
        Account account = accountService.getAccountByNumber(accountNumber);

        if (account == null) {
            log.warn("⚠️ La cuenta {} no existe en la BD", accountNumber);
            model.addAttribute("error", "La cuenta no existe");
            return "user/retiros";
        }

        model.addAttribute("account", account);
        return "user/retiros";
    }

    @PostMapping("/retirement")
    @ResponseBody
    public ResponseEntity<?> withdraw(@RequestBody Map<String, String> payload, HttpSession session) {

        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber"); // 👈 SE USA LA CUENTA DE LA SESIÓN

        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        if (accountNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No hay número de cuenta en sesión"));
        }

        String montoStr = payload.get("monto");
        if (montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Debe ingresar monto"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.retirement(accountNumber, monto, customerId);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Retiro exitoso", "nuevoSaldo", nuevoSaldo));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno"));
        }
    }

    @GetMapping("/account")
    public String account(Model model, HttpSession session) {
        log.info("🔍 Acceso a /transactions/account");
        log.info("  - Session ID: {}", session.getId());
        log.info("  - customerId: {}", session.getAttribute("customerId"));
        log.info("  - accountNumber en sesión: {}", session.getAttribute("accountNumber"));

        // Obtener el customerId Y accountNumber de la sesión
        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber");

        if (customerId == null) {
            log.warn("⚠️ Usuario no autenticado");
            return "redirect:/login";
        }

        try {
            // Obtener información del cliente
            Customer customer = customerService.getCustomerById(customerId);

            Account account = null;

            // Si hay accountNumber en sesión, buscar ESA cuenta específica
            if (accountNumber != null && !accountNumber.isEmpty()) {
                log.info("🔍 Buscando cuenta específica: {}", accountNumber);
                account = accountService.getAccountByNumber(accountNumber);

                if (account != null) {
                    log.info("✅ Cuenta encontrada: {}", account.getAccountNumber());
                } else {
                    log.warn("⚠️ No se encontró la cuenta {} en la BD", accountNumber);
                }
            }

            // Si no se encontró, buscar todas las cuentas y tomar la primera
            if (account == null) {
                log.info("🔍 Buscando todas las cuentas del cliente");
                List<Account> accounts = accountService.getAccountsByCustomerId(customerId);

                if (accounts == null || accounts.isEmpty()) {
                    log.error("❌ El cliente {} no tiene cuentas", customerId);
                    model.addAttribute("error", "No tienes cuentas registradas");
                    model.addAttribute("customer", customer);
                    return "user/cuenta";
                }

                account = accounts.get(0);
                log.info("✅ Mostrando primera cuenta: {}", account.getAccountNumber());
            }

            log.info("👤 Usuario {} viendo cuenta: {}",
                    customer.getUsername(), account.getAccountNumber());

            // Agregar datos al modelo
            model.addAttribute("customer", customer);
            model.addAttribute("account", account);

            return "user/cuenta";

        } catch (Exception e) {
            log.error("❌ Error al cargar cuenta para customerId={}", customerId, e);
            model.addAttribute("error", "Error al cargar la información de la cuenta");
            return "error";
        }
    }

    @GetMapping("/transfer")
    public String transferPage(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");

        if (customerId == null) {
            return "redirect:/login";
        }

        // Cargar número de cuenta del usuario (ya lo guardas en sesión)
        String accountNumber = (String) session.getAttribute("accountNumber");

        model.addAttribute("accountNumber", accountNumber);
        return "user/transferencias";
    }

    @PostMapping("/transfer")
    @ResponseBody
    public ResponseEntity<?> transfer(@RequestBody Map<String, String> payload, HttpSession session) {

        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        // Cuenta origen del usuario logueado
        String cuentaOrigen = (String) session.getAttribute("accountNumber");

        // Ahora solo lees cuenta destino y monto del payload
        String cuentaDestino = payload.get("cuentaDestino");
        String montoStr = payload.get("monto");

        if (cuentaDestino == null || montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos incompletos"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);

            // Llamada a tu servicio de transferencia
            BigDecimal nuevoSaldo = transactionService.transfer(
                    cuentaOrigen,
                    cuentaDestino,
                    monto,
                    customerId);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Transferencia exitosa",
                    "nuevoSaldo", nuevoSaldo));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno del servidor"));
        }
    }

}
