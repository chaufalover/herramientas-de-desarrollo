package com.agente.digitalperu.features.session;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SessionLogDTO {
    private Long id;
    private String customerName;
    private LocalDateTime eventTime;
    private String eventType;
}

