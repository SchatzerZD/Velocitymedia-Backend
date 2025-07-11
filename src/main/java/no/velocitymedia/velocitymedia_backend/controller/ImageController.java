package no.velocitymedia.velocitymedia_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import no.velocitymedia.velocitymedia_backend.model.ImageEntity;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.ImageService;
import no.velocitymedia.velocitymedia_backend.service.ProjectService;

@RequestMapping(value = "/image")
@RestController
public class ImageController {


    @Autowired
    private ImageService imageService;

    @Autowired
    private ProjectService projectService;

    @Value("${upload.image.dir}")
    private String UPLOAD_IMAGE_DIR;


    @PostMapping("/{id}")
    public ResponseEntity<?> upload(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId, @RequestParam("file") MultipartFile file) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
               
        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if(project == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_IMAGE_DIR).toAbsolutePath();
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            
            String uniqueFileName = baseName + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String publicImageUrl = "/media/images/" + uniqueFileName;

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagePath(publicImageUrl);
            imageEntity.setProject(project);

            imageService.uploadImage(imageEntity);
            return ResponseEntity.ok(imageEntity.getImagePath());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Something went wrong: " + e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImages(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId) {
        ProjectEntity projectEntity = projectService.getProjectById(Long.parseLong(projectId));
        if(user == null || projectEntity.getUser().getId() != user.getId()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(imageService.getAllImagesByProject(projectEntity));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getImagesFromUser(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if(project == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        return ResponseEntity.ok(imageService.getAllImagesByProject(projectService.getProjectById(Long.parseLong(projectId))));
    }

}
