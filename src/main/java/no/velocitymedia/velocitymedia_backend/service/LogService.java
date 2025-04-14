package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.LogEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.repository.LogRepository;

@Service
@Transactional
public class LogService {


    @Autowired
    private LogRepository logRepository;

    public List<LogEntity> getAllLogsByUser(UserEntity user){
        return logRepository.findByUserEntity(user);
    }

    public void log(LogEntity logEntity){
        logRepository.save(logEntity);
    }

}
