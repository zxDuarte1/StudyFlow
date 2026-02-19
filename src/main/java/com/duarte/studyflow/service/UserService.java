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
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,EmailService emailService) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.emailService = emailService;
        }
    //Criar Usuario
    public User createUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("Email já cadastrado");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String code = String.valueOf(
                (int) (Math.random() * 90000)
        );

        user.setVerificationCode(code);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        emailService.sendVerificationEmail(
                user.getEmail(),
                code
        );

        return savedUser;
    }

    //Procurar Email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
