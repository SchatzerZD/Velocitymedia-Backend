package no.velocitymedia.velocitymedia_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.ImageEntity;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long>{

    List<ImageEntity> findByProject(ProjectEntity project);

}
