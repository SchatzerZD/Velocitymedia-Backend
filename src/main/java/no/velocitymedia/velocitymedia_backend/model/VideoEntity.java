package no.velocitymedia.velocitymedia_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos",schema = "dbo")
public class VideoEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoName;
    private String filePath;

    public VideoEntity() {
    }

    public VideoEntity(String videoName, String filePath) {
        super();
        this.videoName = videoName;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setFilePath(String file) {
        this.filePath = file;
    }


}
