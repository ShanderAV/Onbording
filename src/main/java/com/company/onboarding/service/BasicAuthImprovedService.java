package com.company.onboarding.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service  // ⬅️ Аннотация Spring, чтобы класс стал бином
public class BasicAuthImprovedService {

    private final RestTemplate restTemplate;

    public BasicAuthImprovedService() {
        this.restTemplate = new RestTemplate();
    }

    public String getWithBasicAuth(String url, String username, String password) {
        HttpHeaders headers = createBasicAuthHeaders(username, password);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    private HttpHeaders createBasicAuthHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        return headers;
    }
}