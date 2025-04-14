package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;
import no.velocitymedia.velocitymedia_backend.repository.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }

    public UserEntity getUserByName(String username){
        return userRepository.findByUsernameIgnoreCase(username).get();
    }

    public void addUser(UserEntity user){
        user.setPassword(encryptionService.encryptPassword(user.getPassword()));
        userRepository.save(user);
    }

}
