package com.agente.digitalperu.util;

import org.springframework.stereotype.Component;

import com.agente.digitalperu.features.session.SessionLogService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SessionTimeoutListener implements HttpSessionListener{
    
    private SessionLogService sessionLogService;
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
         HttpSession session = se.getSession();

        Long customerId = (Long) session.getAttribute("customerId");

        if (customerId != null) {
            sessionLogService.saveLog(customerId, "SESSION_TIMEOUT");
        }
    }
}
