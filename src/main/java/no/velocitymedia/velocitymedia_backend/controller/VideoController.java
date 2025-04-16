package no.velocitymedia.velocitymedia_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import no.velocitymedia.velocitymedia_backend.model.CommentEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.service.CommentService;
import no.velocitymedia.velocitymedia_backend.service.UserService;
import no.velocitymedia.velocitymedia_backend.service.VideoService;

import org.springframework.web.bind.annotation.CrossOrigin;
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

    private final String UPLOAD_VIDEO_DIR = "C:\\Users\\danir\\Documents\\AS film\\frontend\\velocitymedia-frontend\\media\\videos";


    @GetMapping("/")
    public ResponseEntity<?> getVideos(@AuthenticationPrincipal UserEntity user) {
        //TODO:Better admin authentication
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if(!user.getUsername().equals("admin")){
            return ResponseEntity.ok(videoService.getAllByUser(user));
        }

        return ResponseEntity.ok(videoService.findAll());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> uploadVideo(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String userId, @RequestParam("file") MultipartFile file) {

        //TODO:Better admin authentication
        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            UserEntity targetUser = userService.getUserById(Long.parseLong(userId));

            Path uploadPath = Paths.get(UPLOAD_VIDEO_DIR).toAbsolutePath();
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setVideoName(fileName);
            videoEntity.setFilePath(filePath.toString());

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
