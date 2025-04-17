package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.LogEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.LogService;
import no.velocitymedia.velocitymedia_backend.service.UserService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RequestMapping(value = "/log")
@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;


    @PostMapping("/{id}")
    public ResponseEntity<?> log(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId,@RequestBody LogEntity logEntity) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        UserEntity targetUser = userService.getUserById(Long.parseLong(userId));
        if(targetUser == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        }

        try {
            logEntity.getLog().equals(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Log cannot be null");
        }

        logEntity.setUserEntity(targetUser);
        logService.log(logEntity);
        return ResponseEntity.ok(logEntity.getLog());
    }

    @GetMapping("/")
    public ResponseEntity<?> getLogs(@AuthenticationPrincipal UserEntity user) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(logService.getAllLogsByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLogsFromUser(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String id) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UserEntity targetUser = userService.getUserById(Long.parseLong(id));
        if(targetUser == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User doesnt exist");
        }

        return ResponseEntity.ok(logService.getAllLogsByUser(targetUser));
    }
    
    



}
