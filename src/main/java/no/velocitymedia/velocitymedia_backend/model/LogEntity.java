package no.velocitymedia.velocitymedia_backend.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "log", schema = "dbo")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(length = 4000)
    private String log;

    private Date logCreatedDate;

    public LogEntity() {
        this.logCreatedDate = new Date(System.currentTimeMillis());
    }

    public LogEntity(ProjectEntity project, String log){
        super();
        this.project = project;
        this.log = log;
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

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getLogCreatedDate() {
        return logCreatedDate;
    }

    public void setLogCreatedDate(Date logCreatedDate) {
        this.logCreatedDate = logCreatedDate;
    }

    

    
    
    


}
