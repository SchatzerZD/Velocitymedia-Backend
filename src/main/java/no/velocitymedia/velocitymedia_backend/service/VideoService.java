package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
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

    public List<VideoEntity> getAllByUser(UserEntity userEntity){
        return videoRepository.findByUser(userEntity);
    }


    public void addVideo(UserEntity user,String videoName, String filePath){

        if(videoName.equals(null) || filePath.equals(null) || user.equals(null)){
            throw new IllegalArgumentException();
        }
        VideoEntity newVideo = new VideoEntity();
        newVideo.setUser(user);
        newVideo.setVideoName(videoName);
        newVideo.setFilePath(filePath);

        videoRepository.save(newVideo);
    }

    public VideoEntity getVideoById(Long id){
        return videoRepository.findById(id).get();
    }

    public boolean verifyVideoUser(UserEntity user, VideoEntity videoEntity){
        if(user.getId() == videoEntity.getUser().getId()){
            return true;
        }else{
            return false;
        }
    }

}
