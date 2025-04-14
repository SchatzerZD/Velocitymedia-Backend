package no.velocitymedia.velocitymedia_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.velocitymedia.velocitymedia_backend.model.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long>{
    
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

}
