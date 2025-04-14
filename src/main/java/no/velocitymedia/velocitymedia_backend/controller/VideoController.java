package no.velocitymedia.velocitymedia_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.velocitymedia.velocitymedia_backend.model.CommentEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.service.CommentService;
import no.velocitymedia.velocitymedia_backend.service.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping(value = "/video")
@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CommentService commentService;


    @GetMapping("/")
    public ResponseEntity<List<VideoEntity>> getVideos() {
        return ResponseEntity.ok(videoService.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> uploadVideo(@AuthenticationPrincipal UserEntity user, @RequestBody VideoEntity videoEntity) {
        try {
            videoService.addVideo(user, videoEntity.getVideoName(), videoEntity.getFilePath());
            return ResponseEntity.ok().body("Video uploaded");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> comment(@AuthenticationPrincipal UserEntity user, @PathVariable String id, @RequestBody CommentEntity comment) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        VideoEntity videoEntity = videoService.getVideoById(Long.parseLong(id));
        if(videoService.verifyVideoUser(user, videoEntity) && comment.getComment() != null){
            comment.setVideo(videoEntity);
            commentService.comment(comment);
            return ResponseEntity.ok("Comment added");
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Somewent wrong");
        }
        
    }
    
    
    

}
