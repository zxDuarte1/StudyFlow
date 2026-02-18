package com.duarte.studyflow.service;

import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    //injeção de depêndencia via construtor

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //Criar Usuario
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()){
            throw new IllegalStateException("Usuário já existente");
        }
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );
        //retorna o usuario
        return userRepository.save(user);
    }
    //Procurar Email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }


}
