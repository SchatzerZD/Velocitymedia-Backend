package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.CommentEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoEntity;
import no.velocitymedia.velocitymedia_backend.repository.CommentRepository;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void comment(CommentEntity comment){
        commentRepository.save(comment);
    }

    public List<CommentEntity> getCommentsByVideo(VideoEntity videoEntity){
        return commentRepository.findByVideo(videoEntity);
    }


}
