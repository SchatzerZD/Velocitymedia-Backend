package no.velocitymedia.velocitymedia_backend.service;

import java.util.List;
import java.util.Optional;

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

    public UserEntity getUserById(Long id){
        return userRepository.findById(id).get();
    }

    public void addUser(UserEntity user){

        if(userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()){
            throw new IllegalArgumentException();
        }

        user.setPassword(encryptionService.encryptPassword(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUserFikenInfo(UserEntity user, String accountId){
        if(!userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()){
            throw new IllegalArgumentException();
        }

        user.setAccountId(accountId);
        userRepository.save(user);
    }

    public boolean login(UserEntity user){
        Optional<UserEntity> optionalUser = userRepository.findByUsernameIgnoreCase(user.getUsername());
        if(optionalUser.isPresent() && encryptionService.verifyPassword(user.getPassword(), optionalUser.get().getPassword())){
            return true;
        }else{
            return false;
        }
    }



}
