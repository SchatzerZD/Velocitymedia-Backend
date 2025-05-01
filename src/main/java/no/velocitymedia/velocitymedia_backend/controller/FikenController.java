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
                    String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong with token access: " + e.getMessage());
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
                    String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong with companies: " + e.getMessage());
        }
    }

    @PostMapping("/create-contract")
    public ResponseEntity<?> createContract(@RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, Object> request) {
                
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {

            String companySlug = "fiken-demo-gammel-burger-as";

            Map<String, Object> contactPayload = new HashMap<>();
            contactPayload.put("name", request.get("customerName"));
            contactPayload.put("email", request.get("customerEmail"));
            contactPayload.put("customer", true);

            Map<String, String> addressFromRequest = (Map<String, String>) request.get("address");
            contactPayload.put("address", addressFromRequest);
            

            HttpEntity<Map<String, Object>> contactEntity = new HttpEntity<>(contactPayload, headers);
            ResponseEntity<Map> contactResponse = restTemplate.exchange(
                    "https://api.fiken.no/api/v2/companies/" + companySlug + "/contacts",
                    HttpMethod.POST,
                    contactEntity,
                    Map.class);

            String contactHref = contactResponse.getHeaders().getLocation().toString();

            Map<String, Object> invoicePayload = new HashMap<>();
            invoicePayload.put("issueDate", "2025-04-30");
            invoicePayload.put("dueDate", "2025-05-15");
            invoicePayload.put("invoiceText", "Kontrakt: " + request.get("contractText"));
            invoicePayload.put("bankAccountCode", "1920:10001");
            invoicePayload.put("cash", false);
        

            Map<String, String> customerRef = new HashMap<>();
            customerRef.put("href", contactHref);
            invoicePayload.put("customer", customerRef);

            String contactId = contactHref.substring(contactHref.lastIndexOf('/') + 1);
            invoicePayload.put("customerId", contactId);


            List<Map<String, Object>> lines = new ArrayList<>();
            Map<String, Object> lineItem = new HashMap<>();
            lineItem.put("description", "Tjeneste/Produkt");
            lineItem.put("unitPrice", 1000);
            lineItem.put("vatType", "high");
            lineItem.put("quantity", 1);
            lineItem.put("incomeAccount", 3000);
            lines.add(lineItem);

            invoicePayload.put("lines", lines);


            HttpEntity<Map<String, Object>> invoiceEntity = new HttpEntity<>(invoicePayload, headers);
            ResponseEntity<Map> invoiceResponse = restTemplate.exchange(
                    "https://api.fiken.no/api/v2/companies/" + companySlug + "/invoices",
                    HttpMethod.POST,
                    invoiceEntity,
                    Map.class);

            return ResponseEntity.ok(invoiceResponse.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Feil ved kontraktopprettelse: " + e.getMessage());
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
