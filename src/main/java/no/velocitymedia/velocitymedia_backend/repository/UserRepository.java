package no.velocitymedia.velocitymedia_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import java.util.List;


public interface UserRepository extends JpaRepository<UserEntity, Long>{
    
    Optional<UserEntity> findById(Long id);
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

}
