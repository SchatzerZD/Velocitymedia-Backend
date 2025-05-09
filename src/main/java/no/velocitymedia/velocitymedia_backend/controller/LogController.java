package no.velocitymedia.velocitymedia_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.LogEntity;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.service.LogService;
import no.velocitymedia.velocitymedia_backend.service.ProjectService;

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
    private ProjectService projectService;


    @PostMapping("/{id}")
    public ResponseEntity<?> log(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId,@RequestBody LogEntity logEntity) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if(project == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project not found");
        }

        try {
            logEntity.getLog().equals(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Log cannot be null");
        }

        logEntity.setProject(project);
        logService.log(logEntity);
        return ResponseEntity.ok(logEntity.getLog());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLogs(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId) {
        ProjectEntity projectEntity = projectService.getProjectById(Long.parseLong(projectId));
        if(user == null || projectEntity.getUser().getId() != user.getId()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(logService.getAllLogsByProject(projectEntity));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getLogsFromUser(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId) {
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));
        if(project == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project doesnt exist");
        }

        return ResponseEntity.ok(logService.getAllLogsByProject(projectService.getProjectById(Long.parseLong(projectId))));
    }
    
    



}
