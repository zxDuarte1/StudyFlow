package com.duarte.studyflow.service;

import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    //injeção de depêndencia via construtor

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //Criar Usuario
    public User create(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()){
            throw new IllegalStateException("Usuário já existente");
        }
        //retorna o usuario
        return userRepository.save(user);
    }
    //Procurar Email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
