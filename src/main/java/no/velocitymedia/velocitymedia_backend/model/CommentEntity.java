package no.velocitymedia.velocitymedia_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments", schema = "dbo")
public class CommentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    private int timestampInSeconds;

    private String comment;

    public CommentEntity() {
    }

    public CommentEntity(VideoEntity video, String comment, int timestampInSeconds) {
        super();
        this.video = video;
        this.comment = comment;
        this.timestampInSeconds = timestampInSeconds;
    }

    public Long getId() {
        return id;
    }

    public VideoEntity getVideo() {
        return video;
    }

    public void setVideo(VideoEntity video) {
        this.video = video;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTimestampInSeconds() {
        return timestampInSeconds;
    }

    public void setTimestampInSeconds(int timestampInSeconds) {
        this.timestampInSeconds = timestampInSeconds;
    }

}
