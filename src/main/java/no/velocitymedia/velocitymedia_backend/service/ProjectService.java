package no.velocitymedia.velocitymedia_backend.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.ProjectEntity;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.model.VideoFlag;
import no.velocitymedia.velocitymedia_backend.repository.ProjectRepository;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectEntity> getAllProjectsByUser(UserEntity user){
        return projectRepository.findByUser(user);
    }

    public ProjectEntity getProjectById(Long id){
        if(projectRepository.findById(id).isPresent()){
            return projectRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Project id not found");
    }

    public void updateProjectContractDir(ProjectEntity projectEntity, String newDir){
        projectEntity.setContractPath(newDir);
        projectRepository.save(projectEntity);
    }

    public void addProject(UserEntity user,String name){

        if(user == null){
            throw new IllegalArgumentException();
        }

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setUser(user);
        projectEntity.setName(name);
        projectEntity.setProjectCreatedAt(System.currentTimeMillis());

        projectRepository.save(projectEntity);
    }

    public void updateProjectFikenInfo(ProjectEntity projectEntity, String invoiceId){
        projectEntity.setInvoiceId(invoiceId);
        projectRepository.save(projectEntity);
    }

    public void signContract(ProjectEntity projectEntity,Boolean signed){
        projectEntity.setContractSigned(signed);
        projectRepository.save(projectEntity);
    }

    public void setFlags(Long projectId, List<VideoFlag> newFlags){
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow();
        project.setFlags(newFlags);
        projectRepository.save(project);
    }

    
}
