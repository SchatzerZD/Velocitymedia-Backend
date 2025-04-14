package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.repository.VideoRepository;

@Service
@Transactional
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public List<VideoEntity> findAll() {
        return videoRepository.findAll();
    }


    public void addVideo(String videoName, String filePath){
        VideoEntity newVideo = new VideoEntity();
        newVideo.setVideoName(videoName);
        newVideo.setFilePath(filePath);

        videoRepository.save(newVideo);
    }


}
