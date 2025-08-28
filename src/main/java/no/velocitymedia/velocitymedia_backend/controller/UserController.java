package no.velocitymedia.velocitymedia_backend.controller;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import no.velocitymedia.velocitymedia_backend.dto.UserDTO;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoFlag;
import no.velocitymedia.velocitymedia_backend.repository.ProjectRepository;
import no.velocitymedia.velocitymedia_backend.service.JWTService;
import no.velocitymedia.velocitymedia_backend.service.ProjectService;
import no.velocitymedia.velocitymedia_backend.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping(value = "/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Value("${upload.contract.dir}")
    private String UPLOAD_CONTRACT_DIR;

    UserController(ProjectRepository projectRepository) {
    }

    @GetMapping("/")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserEntity user) {
        // TODO:Better admin authentication
        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        List<UserEntity> userList = userService.getAllUsers();
        List<UserDTO> userDTOList = new ArrayList<>();

        userList.forEach(u -> userDTOList.add(new UserDTO(u.getId(), u.getUsername())));

        return ResponseEntity.ok(userList);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserByAuthentication(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId) {
        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        UserEntity userToBeDeleted = userService.getUserById(Long.parseLong(userId));
        userService.deleteUser(userToBeDeleted);
        return ResponseEntity.ok().body(userToBeDeleted.getUsername() + " deleted");
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjectsByUser(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(projectService.getAllProjectsByUser(user));
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<?> getProjectById(@AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String projectId) {
        ProjectEntity projectEntity = projectService.getProjectById(Long.parseLong(projectId));
        System.out.println(projectEntity.getUser().getId() != user.getId());
        System.out.println();
        if (projectEntity.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Project does not belong to the user");
        }

        return ResponseEntity.ok(projectEntity);

    }

    @GetMapping("/projects/admin/project/{id}")
    public ResponseEntity<?> getProjectByIdAdmin(@AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String projectId) {
        ProjectEntity projectEntity = projectService.getProjectById(Long.parseLong(projectId));

        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        return ResponseEntity.ok(projectEntity);

    }

    @GetMapping("/projects/admin/{id}")
    public ResponseEntity<?> getProjectsFromUser(@AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String userId) {
        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        return ResponseEntity.ok(projectService.getAllProjectsByUser(userService.getUserById(Long.parseLong(userId))));
    }

    @PostMapping("/projects")
    public ResponseEntity<?> addProject(@AuthenticationPrincipal UserEntity user, @RequestBody ProjectEntity project) {
        try {
            projectService.addProject(user, project.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Something went wrong: " + e);
        }

        return ResponseEntity.ok(projectService.getAllProjectsByUser(user));
    }

    @PostMapping("/projects/admin/{id}")
    public ResponseEntity<?> addProject(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId,
            @RequestBody ProjectEntity project) {

        if (user == null || user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            projectService.addProject(userService.getUserById(Long.parseLong(userId)), project.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Something went wrong: " + e);
        }

        return ResponseEntity.ok(projectService.getAllProjectsByUser(user));
    }

    @GetMapping("/projects/{id}/flags")
    public ResponseEntity<?> getProjectFlags(@AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String projectId) {
        ProjectEntity projectEntity = projectService.getProjectById(Long.parseLong(projectId));
        if (projectEntity.getUser().getId() != user.getId() && !user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Project does not belong to the user");
        }

        return ResponseEntity.ok(projectEntity.getFlags());
    }

    @PostMapping("/projects/admin/{id}/flags")
    public ResponseEntity<?> updateFlags(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId,
            @RequestBody List<VideoFlag> newFlags) {
        if (!user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        try {
            projectService.setFlags(Long.parseLong(projectId), newFlags);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Something went wrong with updating flags: " + e);
        }

        return ResponseEntity.ok().body(projectService.getProjectById(Long.parseLong(projectId)));
    }

    @PostMapping("/projects/admin/contract/{id}")
    public ResponseEntity<?> uploadContractToProject(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String projectId,
            @RequestParam("file") MultipartFile file) {

        if (user == null || !user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if (project == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files are allowed.");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_CONTRACT_DIR).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));

            String uniqueFileName = baseName + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String publicContractUrl = "/media/contracts/" + uniqueFileName;

            projectService.updateProjectContractDir(projectService.getProjectById(Long.parseLong(projectId)),
                    publicContractUrl);

            return ResponseEntity.ok("Contract uploaded successfully: " + publicContractUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
        if (user == null || user.getUsername().equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            userService.addUser(user);
            return ResponseEntity.ok(jwtService.generateJWT(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Username already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserEntity user) {
        if (!userService.login(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or password is incorrect");
        }

        return ResponseEntity.ok(jwtService.generateJWT(user));
    }

    @PostMapping("/contract/{id}")
    public ResponseEntity<?> signContract(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String projectId,
            HttpServletRequest request) {

        ProjectEntity projectEntity = projectService.getProjectById(Long.parseLong(projectId));

        if (!projectEntity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Project does not belong to the user");
        }

        String ipAddress = extractClientIp(request);

        projectService.signContract(projectEntity, true, ipAddress);

        return ResponseEntity.ok("Contract Signed");
    }

    @PostMapping("/projects/{id}/contract/signature")
    public ResponseEntity<?> appendSignatureToPdf(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") String projectId,
            @RequestParam("signature") MultipartFile signatureFile) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if (project == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        String contractPdfPath = project.getContractPath();
        if (contractPdfPath == null || contractPdfPath.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Contract not found");
        }

        try {
            Path tempSignaturePath = Paths.get(UPLOAD_CONTRACT_DIR, "signature_" + System.currentTimeMillis() + ".png");
            Files.copy(signatureFile.getInputStream(), tempSignaturePath, StandardCopyOption.REPLACE_EXISTING);

            String absolutePdfPath = contractPdfPath.startsWith("/media/contracts")
                    ? contractPdfPath.replaceFirst("/media/contracts", UPLOAD_CONTRACT_DIR)
                    : contractPdfPath;

            File pdfFile = new File(absolutePdfPath);

            PDDocument document = PDDocument.load(pdfFile);

            PDPage newPage = new PDPage(PDRectangle.A4);
            document.addPage(newPage);

            PDImageXObject pdImage = PDImageXObject.createFromFile(tempSignaturePath.toString(), document);
            PDPageContentStream contentStream = new PDPageContentStream(document, newPage,
                    PDPageContentStream.AppendMode.APPEND, true);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(80, 640);
            contentStream.showText("X");
            contentStream.endText();

            contentStream.moveTo(100, 640);
            contentStream.lineTo(300, 640);
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.setLineWidth(1);
            contentStream.stroke();

            contentStream.drawImage(pdImage, 100, 645, 200, 40);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            contentStream.newLineAtOffset(100, 625);
            contentStream.showText("Signatur");
            contentStream.endText();

            contentStream.close();
            document.save(pdfFile);
            document.close();

            Files.delete(tempSignaturePath);

            return ResponseEntity.ok("Signature added to contract successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding the signature: " + e.getMessage());
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }

}
