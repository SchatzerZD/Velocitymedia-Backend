package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.repository.VideoRepository;

public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public List<VideoEntity> findAll() {
        return videoRepository.findAll();
    }


}
