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
import no.velocitymedia.velocitymedia_backend.service.UserService;
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

    @Autowired
    private UserService userService;


    @GetMapping("/")
    public ResponseEntity<List<VideoEntity>> getVideos() {
        return ResponseEntity.ok(videoService.findAll());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> uploadVideo(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId, @RequestBody VideoEntity videoEntity) {

        //TODO:Better admin authentication
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            UserEntity targetUser = userService.getUserById(Long.parseLong(userId));
            videoService.addVideo(targetUser, videoEntity.getVideoName(), videoEntity.getFilePath());
            return ResponseEntity.ok().body("Video uploaded");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> comment(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String videoId, @RequestBody CommentEntity comment) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if(comment.getComment() != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Comment cant be null");
        }

        VideoEntity videoEntity = videoService.getVideoById(Long.parseLong(videoId));
        if(!videoService.verifyVideoUser(user, videoEntity)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        comment.setVideo(videoEntity);
        commentService.comment(comment);
        return ResponseEntity.ok("Comment added");
        
    }
    
    
    

}
