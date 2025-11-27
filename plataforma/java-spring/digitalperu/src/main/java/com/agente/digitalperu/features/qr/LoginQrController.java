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

    /**
     * Página de inicio de sesión
     */
    @GetMapping
    public String inicioSesion() {
        return "/public/inicio-sesion";
    }

    /**
     * PASO 1: Validar QR (número de tarjeta)
     * 
     * POST /login/qr/validate
     * Body: { "numeroTarjeta": "1234567890" }
     * 
     * Response: {
     *   "mensaje": "Tarjeta válida, proceda con validación facial",
     *   "customerId": 1,
     *   "customerName": "Juan Pérez",
     *   "hasFaceRegistered": true
     * }
     */
    @PostMapping("/qr/validate")
    public ResponseEntity<?> validarQR(@RequestBody Map<String, String> payload) {
        String numeroTarjeta = payload.get("numeroTarjeta");
        
        log.info("📱 Validando QR con número de tarjeta: {}", numeroTarjeta);

        // Buscar cliente por número de tarjeta
        Customer customer = accountService.getAccountNumber(numeroTarjeta);
        
        if (customer == null) {
            log.warn("❌ Tarjeta no encontrada: {}", numeroTarjeta);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Tarjeta no registrada"));
        }

        log.info("✅ QR válido para cliente: {} (ID: {})", customer.getName(), customer.getId());
        
        return ResponseEntity.ok(Map.of(
                "mensaje", "Tarjeta válida, proceda con validación facial",
                "customerId", customer.getId(),
                "customerName", customer.getName(),
                "hasFaceRegistered", true
        ));
    }

    /**
     * PASO 3: Autenticación final con contraseña
     * (El PASO 2 de validación facial se hace en FaceController)
     * 
     * POST /login/auth
     * Body: { "customerId": "1", "password": "password123" }
     */
    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        log.info("🔐 Intento de login con contraseña");
        
        String idStr = payload.get("customerId");
        String password = payload.get("password");
        
        if (idStr == null || password == null) {
            log.warn("❌ Datos incompletos en login");
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos incompletos"));
        }

        try {
            Long customerId = Long.valueOf(idStr);
            Customer customer = customerService.getCustomerById(customerId);
            
            String storedPassword = customer.getPassword();
            
            log.info("Validando password para customerId={}", customerId);

            // Verificar contraseña
            if (storedPassword != null && passwordEncoder.matches(password, storedPassword)) {
                log.info("✅ Login exitoso para: {} (ID: {})", customer.getUsername(), customerId);
                
                // Crear autenticación
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                var auth = new UsernamePasswordAuthenticationToken(
                        customer.getUsername(), 
                        null, 
                        authorities
                );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                // Guardar en sesión
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );
                session.setAttribute("customerId", customerId);
                
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Autenticado correctamente",
                        "username", customer.getUsername()
                ));
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
            log.error("❌ Error en login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor"));
        }
    }
}
