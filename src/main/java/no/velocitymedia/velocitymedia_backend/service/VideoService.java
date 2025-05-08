package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoFlag;
import no.velocitymedia.velocitymedia_backend.repository.VideoRepository;

@Service
@Transactional
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public List<VideoEntity> findAll() {
        return videoRepository.findAll();
    }

    public List<VideoEntity> getAllByProject(ProjectEntity projectEntity){
        return videoRepository.findByProject(projectEntity);
    }

    public List<VideoEntity> getAllByProjectAndVideoFlag(ProjectEntity projectEntity, VideoFlag videoFlag){
        return videoRepository.findByProjectAndVideoFlag(projectEntity, videoFlag);
    }


    public void addVideo(ProjectEntity project,String videoName, String filePath, VideoFlag videoFlag){

        if(videoName.equals(null) || filePath.equals(null) || project.equals(null)){
            throw new IllegalArgumentException();
        }
        VideoEntity newVideo = new VideoEntity();
        newVideo.setProject(project);
        newVideo.setVideoName(videoName);
        newVideo.setFilePath(filePath);
        newVideo.setVideoFlag(videoFlag);

        videoRepository.save(newVideo);
    }

    public VideoEntity getVideoById(Long id){
        return videoRepository.findById(id).get();
    }

    public boolean verifyVideoUser(UserEntity user, VideoEntity videoEntity){
        if(user.getId() == videoEntity.getProject().getUser().getId()){
            return true;
        }else{
            return false;
        }
    }

}
