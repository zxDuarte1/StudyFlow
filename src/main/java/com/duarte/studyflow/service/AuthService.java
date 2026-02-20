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
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!user.getVerified()) {
            throw new RuntimeException("Email ainda não verificado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Senha Inválida");
        }
        return jwtService.generateToken(user);
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getVerificationCode() == null || user.getVerificationCodeExpiresAt() == null) {
            throw new RuntimeException("Código de verificação não gerado corretamente");
        }

        if (user.getVerificationCode().equals(code) &&
                user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {

            user.setVerified(true);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Código inválido ou expirado");
        }
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
