package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{id}")
    public ResponseEntity<?> upload(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId,@RequestBody ImageEntity imageEntity) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        UserEntity targetUser = userService.getUserById(Long.parseLong(userId));
        if(targetUser == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        }

        try {
            imageEntity.getImagePath().equals(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Image path cannot be null");
        }

        imageEntity.setUserEntity(targetUser);
        imageService.uploadImage(imageEntity);
        return ResponseEntity.ok(imageEntity.getImagePath());
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
