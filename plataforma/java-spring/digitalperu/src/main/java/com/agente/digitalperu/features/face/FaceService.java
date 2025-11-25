package com.agente.digitalperu.features.face;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaceService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String registerFace(Long userId, String base64Image) {

        Map<String, Object> body = Map.of(
                "userId", userId.toString(),
                "image", base64Image);

        String url = "http://127.0.0.1:8001/face/register";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return (String) response.getBody().get("file");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al registrar rostro: " + e.getMessage());
        }

        return null;
    }
}
