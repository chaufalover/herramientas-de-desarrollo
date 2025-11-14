package com.agente.digitalperu.features.qr;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agente.digitalperu.features.accounts.AccountService;
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

    @GetMapping
    public String inicioSesion(){
        return "/public/inicio-sesion";
    }

    @PostMapping("/qr/validate")
    public ResponseEntity<?> validarQR(@RequestBody Map<String, String> payload) {
        String numeroTarjeta = payload.get("numeroTarjeta");

        Customer customer = accountService.getAccountNumber(numeroTarjeta);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Tarjeta no registrada"));
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Tarjeta válida, ingrese su contraseña",
                "customerId", customer.getId(),
                "customerName", customer.getName()));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        log.info("Login request keys: {}", payload.keySet());
        String idStr = payload.get("customerId");
        String password = payload.get("password");
        if (idStr == null || password == null) {
            log.warn("Datos incompletos en login");
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos incompletos"));
        }

        try {
            Long customerId = Long.valueOf(idStr);
            Customer customer = customerService.getCustomerById(customerId);
            String stored = customer.getPassword();
            log.info("Cliente {} encontrado. stored-present={}, stored-len={}", customerId, stored != null, stored==null?0:stored.length());

            if (stored != null && passwordEncoder.matches(password, stored)) {
                log.info("Password válida para customerId={}", customerId);
                session.setAttribute("customerId", customerId);
                return ResponseEntity.ok(Map.of("mensaje", "Autenticado"));
            } else {
                log.warn("Password inválida para customerId={}", customerId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Contraseña inválida"));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Cliente no encontrado"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "customerId inválido"));
        } catch (Exception e) {
            log.error("Error en login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Error interno"));
        }
    }

}
