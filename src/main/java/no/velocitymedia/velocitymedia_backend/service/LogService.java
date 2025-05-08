package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.LogEntity;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.repository.LogRepository;

@Service
@Transactional
public class LogService {


    @Autowired
    private LogRepository logRepository;

    public List<LogEntity> getAllLogsByProject(ProjectEntity projectEntity){
        return logRepository.findByProject(projectEntity);
    }

    public void log(LogEntity logEntity){
        logRepository.save(logEntity);
    }

}
