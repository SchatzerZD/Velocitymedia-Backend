package no.velocitymedia.velocitymedia_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.VideoEntity;


public interface VideoRepository extends JpaRepository<VideoEntity, Long>{


}
