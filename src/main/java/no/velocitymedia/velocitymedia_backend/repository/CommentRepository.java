package no.velocitymedia.velocitymedia_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.CommentEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;

import java.util.List;


public interface CommentRepository extends JpaRepository<CommentEntity, Long>{

    List<CommentEntity> findByVideo(VideoEntity video);

}
