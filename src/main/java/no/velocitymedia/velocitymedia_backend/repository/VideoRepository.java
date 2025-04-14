package no.velocitymedia.velocitymedia_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.VideoEntity;


public interface VideoRepository extends JpaRepository<VideoEntity, Long>{

    List<VideoEntity> findAll();
    

}
