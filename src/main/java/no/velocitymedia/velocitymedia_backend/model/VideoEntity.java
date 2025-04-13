package no.velocitymedia.velocitymedia_backend.model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "video")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoName;
    private MultipartFile file;

    public VideoEntity() {
    }

    public VideoEntity(String videoName, MultipartFile file) {
        super();
        this.videoName = videoName;
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public String getVideoName() {
        return videoName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }


}
