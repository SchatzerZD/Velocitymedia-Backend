package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.ProjectService;
import no.velocitymedia.velocitymedia_backend.service.UserService;

import java.net.URI;
import java.util.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/fiken")
@CrossOrigin(origins = "*")
public class FikenController {

    @Value("${fiken.api-token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ProjectService projectService;

    private final String COMPANY_SLUG = "fiken-demo-gammel-burger-as";

    @PostMapping("/create-contract/{id}")
    public ResponseEntity<?> createContract(@AuthenticationPrincipal UserEntity user,
            @RequestBody Map<String, Object> request, @PathVariable("id") String projectId) {

        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if (project == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String customerName = (String) request.get("customerName");
            String customerId = findExistingContactId(customerName);

            System.out.println(customerId);

            if (customerId == null) {
                Map<String, Object> contactPayload = new HashMap<>();
                contactPayload.put("name", customerName);
                contactPayload.put("email", request.get("customerEmail"));
                contactPayload.put("customer", true);
                contactPayload.put("address", request.get("address"));

                HttpEntity<Map<String, Object>> contactEntity = new HttpEntity<>(contactPayload, headers);
                ResponseEntity<Map> contactResponse = restTemplate.exchange(
                        "https://api.fiken.no/api/v2/companies/" + COMPANY_SLUG + "/contacts",
                        HttpMethod.POST,
                        contactEntity,
                        Map.class);

                String contactHref = contactResponse.getHeaders().getLocation().toString();
                customerId = contactHref.substring(contactHref.lastIndexOf('/') + 1);
            }

            Map<String, Object> invoicePayload = new HashMap<>();
            invoicePayload.put("type", "invoice");
            invoicePayload.put("issueDate", "2025-04-30");
            invoicePayload.put("daysUntilDueDate", 14);
            invoicePayload.put("invoiceText", "Prosjekt navn: " + request.get("contractText"));
            invoicePayload.put("bankAccountCode", "1920:10001");
            invoicePayload.put("cash", false);
            invoicePayload.put("customerId", customerId);

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
                    "https://api.fiken.no/api/v2/companies/" + COMPANY_SLUG + "/invoices/drafts",
                    HttpMethod.POST,
                    invoiceEntity,
                    Map.class);

            String location = invoiceResponse.getHeaders().getLocation().toString();
            String invoiceId = location.substring(location.lastIndexOf('/') + 1);
            String publicInvoiceUrl = "https://fiken.no/foretak/" + COMPANY_SLUG + "/webfaktura/" + invoiceId;

            ResponseEntity<Map> invoiceBodyResponse = restTemplate.exchange(
                    "https://api.fiken.no/api/v2/companies/" + COMPANY_SLUG + "/invoices/drafts/" + invoiceId,
                    HttpMethod.GET,
                    invoiceEntity,
                    Map.class);

            projectService.updateProjectFikenInfo(project, publicInvoiceUrl);

            return ResponseEntity.ok(Map.of(
                    "invoiceUrl", publicInvoiceUrl,
                    "content", invoiceBodyResponse.getBody()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Feil ved kontraktopprettelse: " + e.getMessage());
        }
    }

    @PostMapping("/update-url/{id}")
    public ResponseEntity<?> updateProjectFikenUrlString(@AuthenticationPrincipal UserEntity user,
            @RequestBody Map<String, Object> request, @PathVariable("id") String projectId) {

        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if (project == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        String newUrl = (String) request.get("url");
        projectService.updateProjectFikenInfo(project, newUrl);

        return ResponseEntity.ok(newUrl);
    }

    private String findExistingContactId(String customerName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.fiken.no/api/v2/companies/" + COMPANY_SLUG + "/contacts";

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class);

            List<Map<String, Object>> contacts = response.getBody();

            if (contacts != null) {
                for (Map<String, Object> contact : contacts) {
                    String name = (String) contact.get("name");
                    if (customerName.equalsIgnoreCase(name)) {
                        Long contactId = (Long) contact.get("contactId");
                        return String.valueOf(contactId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
