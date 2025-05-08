package no.velocitymedia.velocitymedia_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoFlag;


public interface VideoRepository extends JpaRepository<VideoEntity, Long>{

    List<VideoEntity> findByProject(ProjectEntity project);
    List<VideoEntity> findByProjectAndVideoFlag(ProjectEntity project, VideoFlag videoFlag);


}
