package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/fiken")
@CrossOrigin(origins = "*")
public class FikenController {

    @Value("${fiken.client-id}")
    private String clientId;

    @Value("${fiken.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/token")
    public ResponseEntity<?> getAccessToken(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String redirectUri = request.get("redirect_uri");
        String state = request.get("state");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("redirect_uri", redirectUri);
        params.put("state", state);

        HttpEntity<String> entity = new HttpEntity<>(buildUrlEncodedParams(params), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://fiken.no/oauth/token",
                HttpMethod.POST,
                entity,
                String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong with token access: " + e.getMessage());
        }
    }

    @GetMapping("/companies")
    public ResponseEntity<?> getInvoices(@RequestHeader("Authorization") String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.fiken.no/api/v2/companies",
                HttpMethod.GET,
                entity,
                String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong with companies: " + e.getMessage());
        }
    }

    private String buildUrlEncodedParams(Map<String, String> params) {
        StringBuilder encoded = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            encoded.append(entry.getKey())
                   .append('=')
                   .append(entry.getValue())
                   .append('&');
        }
        return encoded.substring(0, encoded.length() - 1);
    }
}
