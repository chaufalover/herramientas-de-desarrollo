package com.agente.digitalperu.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@Slf4j
public class GeocodingService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String obtenerDireccion(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            return null;
        }
        
        try {
            String url = String.format(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=18&addressdetails=1",
                latitud, longitud
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "AgenteDigitalPeru/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            if (response.getBody() != null && response.getBody().containsKey("display_name")) {
                return (String) response.getBody().get("display_name");
            }
            
            return String.format("Lat: %.6f, Lon: %.6f", latitud, longitud);
            
        } catch (Exception e) {
            log.error("❌ Error en geocodificación: {}", e.getMessage());
            return String.format("Lat: %.6f, Lon: %.6f", latitud, longitud);
        }
    }
    
    public String generarEnlaceMaps(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            return null;
        }
        return String.format("https://www.google.com/maps?q=%f,%f", latitud, longitud);
    }
}
