package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.UserService;

import java.net.URI;
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

    @Autowired
    private UserService userService;

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

@GetMapping("/get-contract/{invoiceId}")
    public ResponseEntity<?> getContract(
            @PathVariable String invoiceId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String companySlug = "fiken-demo-gammel-burger-as";
        String url = String.format("https://api.fiken.no/api/v2/companies/%s/invoices/%s", companySlug, invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseEntity.ok().body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Something went wrong retrieving the invoice: " + e.getMessage());
        }
    }
    

    @PostMapping("/create-contract")
    public ResponseEntity<?> createContract(@AuthenticationPrincipal UserEntity user, @RequestBody Map<String, Object> request) {
                
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + (String) request.get("accessToken"));
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

            String location = invoiceResponse.getHeaders().getLocation().toString();
            String invoiceId = location.substring(location.lastIndexOf('/') + 1);
            String publicInvoiceUrl = "https://fiken.no/foretak/" + companySlug + "/webfaktura/" + invoiceId;

            ResponseEntity<Map> invoiceBodyResponse = restTemplate.exchange(
                    "https://api.fiken.no/api/v2/companies/" + companySlug + "/invoices/" + invoiceId,
                    HttpMethod.GET,
                    invoiceEntity,
                    Map.class);


            userService.updateUserFikenInfo(user, contactId, invoiceId);
                    
            return ResponseEntity.ok(Map.of("invoiceUrl", publicInvoiceUrl.toString(), "content", invoiceBodyResponse.getBody()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Feil ved kontraktopprettelse: " + e.getMessage());
        }
    }

    @PostMapping("/user/info")
    public ResponseEntity<?> updateUserFikenInfo(@AuthenticationPrincipal UserEntity user, @RequestBody Map<String, String> request) {
        
        try {
            String accountId = request.get("accountId");
            String invoiceId = request.get("invoiceId");     
            userService.updateUserFikenInfo(user, accountId, invoiceId);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Something went wrong updating user: " + e);
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
