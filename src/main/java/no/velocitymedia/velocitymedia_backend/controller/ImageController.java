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
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.ImageService;
import no.velocitymedia.velocitymedia_backend.service.UserService;

@RequestMapping(value = "/image")
@RestController
public class ImageController {


    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Value("${upload.image.dir}")
    private String UPLOAD_IMAGE_DIR;


    @PostMapping("/{id}")
    public ResponseEntity<?> upload(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId, @RequestParam("file") MultipartFile file) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
               
        UserEntity targetUser = userService.getUserById(Long.parseLong(userId));
        if(targetUser == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_IMAGE_DIR).toAbsolutePath();
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagePath(filePath.toString());
            imageEntity.setUserEntity(targetUser);

            imageService.uploadImage(imageEntity);
            return ResponseEntity.ok(imageEntity.getImagePath());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Something went wrong: " + e);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getImages(@AuthenticationPrincipal UserEntity user) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(imageService.getAllImagesByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImagesFromUser(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String id) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UserEntity targetUser = userService.getUserById(Long.parseLong(id));
        if(targetUser == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User doesnt exist");
        }

        return ResponseEntity.ok(imageService.getAllImagesByUser(targetUser));
    }

}
