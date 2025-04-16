package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.JWTService;
import no.velocitymedia.velocitymedia_backend.service.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping(value = "/user")
@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserEntity user) {
        //TODO:Better admin authentication
        if(!user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

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


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserEntity user) {
        if(!userService.login(user)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or password is incorrect");
        }
        
        return ResponseEntity.ok(jwtService.generateJWT(user));
    }
    
    
    

}
