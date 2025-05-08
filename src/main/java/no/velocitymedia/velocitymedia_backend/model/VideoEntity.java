package no.velocitymedia.velocitymedia_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos",schema = "dbo")
public class VideoEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    private String videoName;
    private String filePath;

    @Enumerated
    @Column(name = "video_flag")
    private VideoFlag videoFlag;

    public VideoEntity() {
    }

    public VideoEntity(ProjectEntity project, String videoName, String filePath, VideoFlag videoFlag) {
        super();
        this.project = project;
        this.videoName = videoName;
        this.filePath = filePath;
        this.videoFlag = videoFlag;
    }

    public Long getId() {
        return id;
    }


    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public VideoFlag getVideoFlag() {
        return videoFlag;
    }

    public void setVideoFlag(VideoFlag videoFlag) {
        this.videoFlag = videoFlag;
    }

    

}
