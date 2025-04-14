package no.velocitymedia.velocitymedia_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping(value = "/user")
@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    

}
