package com.agente.digitalperu.features.session;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SessionLogService {

    private SessionLogRepository repo;

    public void saveLog(Long customerId, String type) {
        SessionLog log = new SessionLog();
        log.setCustomerId(customerId);
        log.setEventType(type);
        repo.save(log);
    }

    public List<SessionLog> findAll(){
        return repo.findAll();
    }
}
