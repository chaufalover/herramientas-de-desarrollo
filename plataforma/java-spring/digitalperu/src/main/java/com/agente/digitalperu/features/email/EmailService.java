package com.agente.digitalperu.features.email;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    // guarda codigo temporalmente
    private final Map<Long, VerificationData> verificationCodes = new ConcurrentHashMap<>();

    public String generateAndSendCode(Long customerId, String email, String customerName) {
        try {
            
            String code = generateCode();
            
            VerificationData data = new VerificationData(code, System.currentTimeMillis());
            verificationCodes.put(customerId, data);
            
            log.info("📧 Generando código {} para cliente {} ({})", code, customerId, email);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Código de Verificación - Agente Digital Perú");
            message.setText(String.format(
                "Hola %s,\n\n" +
                "Tu código de verificación es: %s\n\n" +
                "Este código expirará en 1 minutos.\n\n" +
                "Si no solicitaste este código, ignora este mensaje.\n\n" +
                "Saludos,\n" +
                "Agente Digital Perú",
                customerName, code
            ));
            
            mailSender.send(message);
            
            log.info("✅ Email enviado exitosamente a {}", email);
            return code;
            
        } catch (Exception e) {
            log.error("❌ Error al enviar email: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar código de verificación");
        }
    }

    
    public boolean validateCode(Long customerId, String inputCode) {
        VerificationData data = verificationCodes.get(customerId);
        
        if (data == null) {
            log.warn("⚠️ No existe código para customerId: {}", customerId);
            return false;
        }
        
        long elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(
            System.currentTimeMillis() - data.getTimestamp()
        );
        
        if (elapsedMinutes > 1) {
            log.warn("⏱️ Código expirado para customerId: {}", customerId);
            verificationCodes.remove(customerId);
            return false;
        }
        
        boolean isValid = data.getCode().equals(inputCode);
        
        if (isValid) {
            log.info("✅ Código válido para customerId: {}", customerId);
            verificationCodes.remove(customerId); 
        } else {
            log.warn("❌ Código inválido para customerId: {}. Esperado: {}, Recibido: {}", 
                    customerId, data.getCode(), inputCode);
        }
        
        return isValid;
    }

  
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); 
        return String.valueOf(code);
    }

   
    public void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        verificationCodes.entrySet().removeIf(entry -> {
            long elapsed = TimeUnit.MILLISECONDS.toMinutes(now - entry.getValue().getTimestamp());
            return elapsed > 5;
        });
    }

    // envio de email con pdf adjunto
      public void sendEmailWithPdfAndLocation(
            String to, 
            String subject, 
            String body, 
            byte[] pdfContent, 
            String pdfFileName,
            String ubicacion,
            String enlaceMaps) {
        
        try {
            log.info("📧 Enviando email con PDF y ubicación a: {}", to);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            
            // Construir el body con la ubicación
            StringBuilder fullBody = new StringBuilder(body);
            
            if (ubicacion != null && !ubicacion.isEmpty()) {
                fullBody.append("\n\n");
                fullBody.append("═══════════════════════════════════════\n");
                fullBody.append("📍 UBICACIÓN DE LA TRANSACCIÓN\n");
                fullBody.append("═══════════════════════════════════════\n");
                fullBody.append(ubicacion);
                
                if (enlaceMaps != null) {
                    fullBody.append("\n\n");
                    fullBody.append("🗺️ Ver en Google Maps:\n");
                    fullBody.append(enlaceMaps);
                }
            }
            
            helper.setText(fullBody.toString(), false);
            
            // Adjuntar PDF
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfContent, "application/pdf");
            helper.addAttachment(pdfFileName, dataSource);
            
            mailSender.send(message);
            
            log.info("✅ Email con PDF y ubicación enviado exitosamente a {}", to);
            
        } catch (MessagingException e) {
            log.error("❌ Error al enviar email con PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar comprobante por email");
        }
    }
    
    // clase interna para datos de verificación
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class VerificationData {
        private String code;
        private long timestamp;
    }
    
}
