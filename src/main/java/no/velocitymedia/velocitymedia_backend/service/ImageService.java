package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.ImageEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.repository.ImageRepository;

@Service
@Transactional
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public List<ImageEntity> getAllImagesByUser(UserEntity user){
        return imageRepository.findByUserEntity(user);
    }

    public void uploadImage(ImageEntity imageEntity){
        imageRepository.save(imageEntity);
    }

}
