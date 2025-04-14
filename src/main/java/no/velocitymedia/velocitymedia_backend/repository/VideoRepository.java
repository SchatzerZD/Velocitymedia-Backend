package no.velocitymedia.velocitymedia_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.VideoEntity;


public interface VideoRepository extends JpaRepository<VideoEntity, Long>{


    @Override
    default List<VideoEntity> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }


}
