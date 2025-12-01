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
import com.agente.digitalperu.features.email.EmailService;
import com.agente.digitalperu.util.GeocodingService;
import com.agente.digitalperu.util.PdfGeneratorService;

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
    private final EmailService emailService;
    private final PdfGeneratorService pdfGeneratorService;
    private final GeocodingService geocodingService;

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
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> payload, HttpSession session) {

        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber");

        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        if (accountNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No hay número de cuenta en sesión"));
        }

        String montoStr = payload.get("monto").toString();
        if (montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Debe ingresar monto"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.deposit(accountNumber, monto, customerId);

            Customer customer = customerService.getCustomerById(customerId);
            String transactionId = "DEP-" + System.currentTimeMillis();

            // 👇 CAPTURAR UBICACIÓN
            Double latitud = payload.get("latitud") != null ? Double.parseDouble(payload.get("latitud").toString())
                    : null;
            Double longitud = payload.get("longitud") != null ? Double.parseDouble(payload.get("longitud").toString())
                    : null;

            String ubicacion = null;
            String enlaceMaps = null;

            if (latitud != null && longitud != null) {
                ubicacion = geocodingService.obtenerDireccion(latitud, longitud);
                enlaceMaps = geocodingService.generarEnlaceMaps(latitud, longitud);
                log.info("📍 Ubicación capturada: {}", ubicacion);
            }

            try {
                byte[] pdfBytes = pdfGeneratorService.generateDepositReceipt(
                        customer.getName() + " " + customer.getLastName(),
                        accountNumber,
                        monto,
                        nuevoSaldo,
                        transactionId);

                String subject = "Comprobante de Depósito - Agente Digital Perú";
                String body = String.format(
                        "Estimado/a %s,\n\n" +
                                "Se ha realizado un depósito exitoso en su cuenta.\n\n" +
                                "Monto: S/ %,.2f\n" +
                                "Nuevo saldo: S/ %,.2f\n\n" +
                                "Adjunto encontrará el comprobante de la operación.\n\n" +
                                "Saludos,\n" +
                                "Agente Digital Perú",
                        customer.getName(),
                        monto,
                        nuevoSaldo);

                // 👇 ENVIAR CON UBICACIÓN
                emailService.sendEmailWithPdfAndLocation(
                        customer.getEmail(),
                        subject,
                        body,
                        pdfBytes,
                        "Comprobante_Deposito_" + transactionId + ".pdf",
                        ubicacion,
                        enlaceMaps);

                log.info("✅ Comprobante enviado al email: {}", customer.getEmail());

            } catch (Exception e) {
                log.error("❌ Error al generar/enviar comprobante: {}", e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Depósito exitoso. Comprobante enviado a su email.",
                    "nuevoSaldo", nuevoSaldo,
                    "transactionId", transactionId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error en depósito", e);
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
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> payload, HttpSession session) {

        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber");

        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "No autenticado"));
        }

        if (accountNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No hay número de cuenta en sesión"));
        }

        String montoStr = payload.get("monto").toString();
        if (montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Debe ingresar monto"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.retirement(accountNumber, monto, customerId);

            Customer customer = customerService.getCustomerById(customerId);
            String transactionId = "RET-" + System.currentTimeMillis();

            // 👇 CAPTURAR UBICACIÓN
            Double latitud = payload.get("latitud") != null ? Double.parseDouble(payload.get("latitud").toString())
                    : null;
            Double longitud = payload.get("longitud") != null ? Double.parseDouble(payload.get("longitud").toString())
                    : null;

            String ubicacion = null;
            String enlaceMaps = null;

            if (latitud != null && longitud != null) {
                ubicacion = geocodingService.obtenerDireccion(latitud, longitud);
                enlaceMaps = geocodingService.generarEnlaceMaps(latitud, longitud);
            }

            try {
                byte[] pdfBytes = pdfGeneratorService.generateWithdrawalReceipt(
                        customer.getName() + " " + customer.getLastName(),
                        accountNumber,
                        monto,
                        nuevoSaldo,
                        transactionId);

                String subject = "Comprobante de Retiro - Agente Digital Perú";
                String body = String.format(
                        "Estimado/a %s,\n\n" +
                                "Se ha realizado un retiro exitoso de su cuenta.\n\n" +
                                "Monto: S/ %,.2f\n" +
                                "Nuevo saldo: S/ %,.2f\n\n" +
                                "Adjunto encontrará el comprobante de la operación.\n\n" +
                                "Saludos,\n" +
                                "Agente Digital Perú",
                        customer.getName(),
                        monto,
                        nuevoSaldo);

                emailService.sendEmailWithPdfAndLocation(
                        customer.getEmail(),
                        subject,
                        body,
                        pdfBytes,
                        "Comprobante_Retiro_" + transactionId + ".pdf",
                        ubicacion,
                        enlaceMaps);

                log.info("✅ Comprobante enviado al email: {}", customer.getEmail());

            } catch (Exception e) {
                log.error("❌ Error al generar/enviar comprobante: {}", e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Retiro exitoso. Comprobante enviado a su email.",
                    "nuevoSaldo", nuevoSaldo,
                    "transactionId", transactionId));

         } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error en depósito", e);
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno"));
        }
    }

    @GetMapping("/account")
    public String account(Model model, HttpSession session) {
        log.info("🔍 Acceso a /transactions/account");
        log.info("  - Session ID: {}", session.getId());
        log.info("  - customerId: {}", session.getAttribute("customerId"));
        log.info("  - accountNumber en sesión: {}", session.getAttribute("accountNumber"));

        Long customerId = (Long) session.getAttribute("customerId");
        String accountNumber = (String) session.getAttribute("accountNumber");

        if (customerId == null) {
            log.warn("⚠️ Usuario no autenticado");
            return "redirect:/login";
        }

        try {
            Customer customer = customerService.getCustomerById(customerId);

            Account account = null;

            if (accountNumber != null && !accountNumber.isEmpty()) {
                log.info("🔍 Buscando cuenta específica: {}", accountNumber);
                account = accountService.getAccountByNumber(accountNumber);

                if (account != null) {
                    log.info("✅ Cuenta encontrada: {}", account.getAccountNumber());
                } else {
                    log.warn("⚠️ No se encontró la cuenta {} en la BD", accountNumber);
                }
            }

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

        String cuentaOrigen = (String) session.getAttribute("accountNumber");

        String cuentaDestino = payload.get("cuentaDestino");
        String montoStr = payload.get("monto");

        if (cuentaDestino == null || montoStr == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos incompletos"));
        }

        Account cuentaDestinoObj = accountService.getAccountByNumber(cuentaDestino);
        if (cuentaDestinoObj == null) {
            return ResponseEntity.status(404).body(
                    Map.of("mensaje", "La cuenta destino no existe"));
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            BigDecimal nuevoSaldo = accountService.retirement(cuentaOrigen, monto, customerId);

            Customer customer = customerService.getCustomerById(customerId);

            String transactionId = "TR-" + System.currentTimeMillis();

            Double latitud = payload.get("latitud") != null ? Double.parseDouble(payload.get("latitud").toString())
                    : null;
            Double longitud = payload.get("longitud") != null ? Double.parseDouble(payload.get("longitud").toString())
                    : null;

            String ubicacion = null;
            String enlaceMaps = null;

            if (latitud != null && longitud != null) {
                ubicacion = geocodingService.obtenerDireccion(latitud, longitud);
                enlaceMaps = geocodingService.generarEnlaceMaps(latitud, longitud);
            }

            try {
                byte[] pdfBytes = pdfGeneratorService.generateTransferReceipt(
                        customer.getName() + " " + customer.getLastName(),
                        cuentaOrigen,
                        cuentaDestino,
                        monto,
                        nuevoSaldo,
                        transactionId);

                String subject = "Comprobante de Transferencia - Agente Digital Perú";
                String body = String.format(
                        "Estimado/a %s,\n\n" +
                                "Se ha realizado una transferencia exitosa de su cuenta.\n\n" +
                                "Monto: S/ %,.2f\n" +
                                "Nuevo saldo: S/ %,.2f\n\n" +
                                "Adjunto encontrará el comprobante de la operación.\n\n" +
                                "Saludos,\n" +
                                "Agente Digital Perú",
                        customer.getName(),
                        monto,
                        nuevoSaldo);

                emailService.sendEmailWithPdfAndLocation(
                        customer.getEmail(),
                        subject,
                        body,
                        pdfBytes,
                        "Comprobante_Retiro_" + transactionId + ".pdf",
                        ubicacion,
                        enlaceMaps);

                log.info("✅ Comprobante enviado al email: {}", customer.getEmail());

            } catch (Exception e) {
                log.error("❌ Error al generar/enviar comprobante: {}", e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Transferencia exitosa. Comprobante enviado a su email.",
                    "nuevoSaldo", nuevoSaldo,
                    "transactionId", transactionId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error en retiro", e);
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error interno"));
        }

    }

    @GetMapping("/history")
    public String showHistory(Model model, HttpSession session) {

        String accountNumber = (String) session.getAttribute("accountNumber");

        if (accountNumber == null) {
            return "redirect:/login";
        }

        List<TransactionHistoryDTO> historial = transactionService.getHistory(accountNumber);

        model.addAttribute("historial", historial);
        model.addAttribute("accountNumber", accountNumber);

        return "user/historial";
    }

}
