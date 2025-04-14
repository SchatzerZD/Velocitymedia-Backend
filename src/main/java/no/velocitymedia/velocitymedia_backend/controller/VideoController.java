package no.velocitymedia.velocitymedia_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.service.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping(value = "/video")
@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;


    @GetMapping("/")
    public ResponseEntity<List<VideoEntity>> getVideos() {
        return ResponseEntity.ok(videoService.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> uploadVideo(@RequestParam String videoName, @RequestParam String videoFilePath) {
        try {
            videoService.addVideo(videoName,videoFilePath);
            return ResponseEntity.ok().body("Video added");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }
    
    

}
