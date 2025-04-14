package no.velocitymedia.velocitymedia_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>{

}
