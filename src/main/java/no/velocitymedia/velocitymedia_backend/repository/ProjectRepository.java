package no.velocitymedia.velocitymedia_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>{

    List<ProjectEntity> findByUser(UserEntity user);

}
