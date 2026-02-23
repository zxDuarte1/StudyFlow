package com.duarte.studyflow.service;

import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public User createUser(User user) {

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("Email já cadastrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setVerificationCode(code);

        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setVerified(false);

        emailService.sendVerificationEmail(user.getEmail(), code);
        return userRepository.save(user);
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
