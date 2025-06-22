package no.velocitymedia.velocitymedia_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import no.velocitymedia.velocitymedia_backend.model.CommentEntity;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoFlag;
import no.velocitymedia.velocitymedia_backend.service.CommentService;
import no.velocitymedia.velocitymedia_backend.service.ProjectService;
import no.velocitymedia.velocitymedia_backend.service.VideoService;
import no.velocitymedia.velocitymedia_backend.service.generators.ThumbnailGenerator;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    private ProjectService projectService;

    @Value("${upload.video.dir}")
    private String UPLOAD_VIDEO_DIR;


    @GetMapping("/{id}/{flag}")
    public ResponseEntity<?> getVideosByFlag(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId, @PathVariable("flag") VideoFlag videoFlag) {

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if(!user.getUsername().equals("admin")){
            return ResponseEntity.ok(videoService.getAllByProjectAndVideoFlag(projectService.getProjectById(Long.parseLong(projectId)), videoFlag));
        }

        return ResponseEntity.ok(videoService.findAll());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> uploadVideo(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String projectId, 
    @RequestParam("file") MultipartFile file, @RequestParam("flag") VideoFlag videoFlag) {

        if(user == null || !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            ProjectEntity project = projectService.getProjectById(Long.parseLong(projectId));

            Path uploadPath = Paths.get(UPLOAD_VIDEO_DIR).toAbsolutePath();
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            
            String uniqueFileName = baseName + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            
            Files.copy(file.getInputStream(), filePath);
            


            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setVideoName(uniqueFileName);
            videoEntity.setFilePath(filePath.toString());
            videoEntity.setVideoFlag(videoFlag);

            String thumbnailFileName = uniqueFileName.replaceAll("\\.mp4$", "") + ".jpg";
            Path thumbnailPath = uploadPath.resolve(thumbnailFileName);
            
            ThumbnailGenerator.generateThumbnail(filePath.toString(), thumbnailPath.toString());

            videoService.addVideo(project, videoEntity.getVideoName(), videoEntity.getFilePath(), videoEntity.getVideoFlag());
            return ResponseEntity.ok().body("Video uploaded");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e);
        }
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<?> getComments(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String videoId) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, user is null");
        }

        VideoEntity videoEntity = videoService.getVideoById(Long.parseLong(videoId));
        if(!videoService.verifyVideoUser(user, videoEntity) && !user.getUsername().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        return ResponseEntity.ok(commentService.getCommentsByVideo(videoEntity));
    }
    

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> comment(@AuthenticationPrincipal UserEntity user, @PathVariable("id") String videoId, @RequestBody CommentEntity comment) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, user is null");
        }


        if(comment.getComment() == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Comment cant be null");
        }

        VideoEntity videoEntity = videoService.getVideoById(Long.parseLong(videoId));
        if(!videoService.verifyVideoUser(user, videoEntity)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        comment.setVideo(videoEntity);
        commentService.comment(comment);
        return ResponseEntity.ok("Comment added:" + comment);
        
    }

    @ControllerAdvice
    public class GlobalControllerExceptionHandler {
    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

    
    
    

}
