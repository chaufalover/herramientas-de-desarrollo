package com.agente.digitalperu.features.qr;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agente.digitalperu.features.accounts.Account;
import com.agente.digitalperu.features.accounts.AccountService;
import com.agente.digitalperu.features.email.EmailService;
import com.agente.digitalperu.features.customers.Customer;
import com.agente.digitalperu.features.customers.CustomerService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginQrController {

    private static final Logger log = LoggerFactory.getLogger(LoginQrController.class);

    private final AccountService accountService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @GetMapping
    public String inicioSesion() {
        return "/public/inicio-sesion";
    }

    @PostMapping("/qr/validate")
    public ResponseEntity<?> validarQR(@RequestBody Map<String, String> payload, HttpSession session) {
        String numeroTarjeta = payload.get("numeroTarjeta");

        log.info("📱 Validando QR con número de tarjeta: {}", numeroTarjeta);

        Customer customer = accountService.getAccountNumber(numeroTarjeta);

        if (customer == null) {
            log.warn("❌ Tarjeta no encontrada: {}", numeroTarjeta);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Tarjeta no registrada"));
        }

        session.setAttribute("accountNumber", numeroTarjeta);

        log.info("✅ QR válido para cliente: {} (ID: {})", customer.getName(), customer.getId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Tarjeta válida",
                "customerId", customer.getId(),
                "customerName", customer.getName(),
                "email", customer.getEmail(),
                "accountNumber", numeroTarjeta 
        ));
    }

    @PostMapping("/password")
    public ResponseEntity<?> validatePassword(@RequestBody Map<String, String> payload, HttpSession session) {
        log.info("🔐 Validando contraseña");

        String idStr = payload.get("customerId");
        String password = payload.get("password");
        String accountNumber = payload.get("accountNumber"); 

        if (idStr == null || password == null) {
            log.warn("❌ Datos incompletos");
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos incompletos"));
        }

        try {
            Long customerId = Long.valueOf(idStr);
            Customer customer = customerService.getCustomerById(customerId);

            String storedPassword = customer.getPassword();

            if (storedPassword != null && passwordEncoder.matches(password, storedPassword)) {
                log.info("✅ Contraseña correcta para: {}", customer.getUsername());

                if (accountNumber != null) {
                    session.setAttribute("accountNumber", accountNumber);
                    log.info("📝 AccountNumber guardado en sesión: {}", accountNumber);

                    Account account = accountService.getAccountByNumber(accountNumber);
                    if (account != null && account.getType() != null) {
                        String accountType = account.getType().getName();
                        session.setAttribute("accountType", accountType);
                        log.info("📝 AccountType guardado en sesión: {}", accountType);
                    }

                }

                try {
                    emailService.generateAndSendCode(
                            customerId,
                            customer.getEmail(),
                            customer.getName());

                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "mensaje", "Código enviado a tu email: " + maskEmail(customer.getEmail())));

                } catch (Exception e) {
                    log.error("❌ Error al enviar código: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of(
                                    "success", false,
                                    "mensaje", "Error al enviar código de verificación"));
                }

            } else {
                log.warn("❌ Contraseña incorrecta para customerId={}", customerId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("mensaje", "Contraseña incorrecta"));
            }

        } catch (NoSuchElementException e) {
            log.error("❌ Cliente no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Cliente no encontrado"));
        } catch (NumberFormatException e) {
            log.error("❌ CustomerId inválido: {}", idStr);
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "CustomerId inválido"));
        } catch (Exception e) {
            log.error("❌ Error en validación de contraseña", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor"));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        log.info("📧 Validando código de verificación");
        log.info("📦 Payload recibido: {}", payload);

        String idStr = payload.get("customerId");
        String code = payload.get("code");

        log.info("  - customerId raw: '{}'", idStr);
        log.info("  - code raw: '{}'", code);
        log.info("  - customerId null? {}", idStr == null);
        log.info("  - code null? {}", code == null);

        if (idStr == null || code == null) {
            log.warn("❌ Datos incompletos. customerId={}, code={}", idStr, code);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Datos incompletos"));
        }

        try {
            Long customerId = Long.valueOf(idStr);

            boolean isValid = emailService.validateCode(customerId, code);

            if (isValid) {
                Customer customer = customerService.getCustomerById(customerId);

                log.info("✅ Código válido. Creando sesión para: {}", customer.getUsername());

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                var auth = new UsernamePasswordAuthenticationToken(
                        customer.getUsername(),
                        null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext());
                session.setAttribute("customerId", customerId);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "mensaje", "Autenticado correctamente",
                        "username", customer.getUsername(),
                        "redirectUrl", "/transactions" // ← Agregar URL de redirect
                ));
            } else {
                log.warn("❌ Código inválido para customerId={}", customerId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "mensaje", "Código inválido o expirado"));
            }

        } catch (NoSuchElementException e) {
            log.error("❌ Cliente no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "mensaje", "Cliente no encontrado"));
        } catch (NumberFormatException e) {
            log.error("❌ CustomerId inválido: {}", idStr);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "mensaje", "CustomerId inválido"));
        } catch (Exception e) {
            log.error("❌ Error en verificación de código", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "mensaje", "Error interno del servidor"));
        }
    }

    private String maskEmail(String email) {
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