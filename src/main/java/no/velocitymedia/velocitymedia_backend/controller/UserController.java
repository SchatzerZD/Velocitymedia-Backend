package no.velocitymedia.velocitymedia_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping(value = "/user")
@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
        try {
            userService.addUser(user);
            return ResponseEntity.ok().body("User added");
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Username already exists");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e);
        }
    }
    
    

}
