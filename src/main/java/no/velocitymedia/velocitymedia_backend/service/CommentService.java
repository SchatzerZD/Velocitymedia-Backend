package no.velocitymedia.velocitymedia_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.CommentEntity;
import no.velocitymedia.velocitymedia_backend.repository.CommentRepository;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void comment(CommentEntity comment){
        commentRepository.save(comment);
    }


}
