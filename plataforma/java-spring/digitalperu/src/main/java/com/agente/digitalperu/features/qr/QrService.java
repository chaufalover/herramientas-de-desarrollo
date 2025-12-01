package com.agente.digitalperu.features.qr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QrService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String QR_API_URL = "http://127.0.0.1:8000/generate";

    public String generateQr(String accountNumber) {
        Map<String, String> request = new HashMap<>();
        request.put("account_number", accountNumber);

        ResponseEntity<Map> response = restTemplate.postForEntity(QR_API_URL, request, Map.class);
        return (String) response.getBody().get("qr_image");
    }

}
