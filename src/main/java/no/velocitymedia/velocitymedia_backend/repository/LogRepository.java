package no.velocitymedia.velocitymedia_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.LogEntity;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;

public interface LogRepository extends JpaRepository<LogEntity, Long>{

    List<LogEntity> findByProject(ProjectEntity project);

}
