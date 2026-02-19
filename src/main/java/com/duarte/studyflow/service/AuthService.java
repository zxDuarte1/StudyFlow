package com.duarte.studyflow.service;

import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!user.getVerified()) {
            throw new RuntimeException("Email ainda não verificado");
        }
        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Senha Inválida");
        }

        return user;
    }
    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public User verifyEmail(String email,String code){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if(user.getVerificationCode() == null){
            throw new RuntimeException("Nenhum Código ativo");
        }
        if(user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Código expirado");
        }
        if(!user.getVerificationCode().equals(code)){
            throw new RuntimeException("Código inválido");
        }
        user.setVerified(true);
        user.setVerificationCode(null);

        return userRepository.save(user);
    }
    public void resendVerificationCode(String email){
        User user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if(user.getVerified()){
            throw new RuntimeException("Usuário já verificado");
        }

        String codeResend = generateCode();
        user.setVerificationCode(codeResend);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
    }
}
