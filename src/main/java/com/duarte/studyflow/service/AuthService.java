package com.duarte.studyflow.service;

import com.duarte.studyflow.model.SecurityLog;
import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.SecurityLogRepository;
import com.duarte.studyflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final SecurityLogRepository securityLogRepository;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService, SecurityLogRepository securityLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.securityLogRepository = securityLogRepository;
    }



    private String generateCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }


    public String login(String email, String password, HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        User user = userRepository.findByEmail(email).orElseGet(() -> {

            securityLogRepository.save(new SecurityLog(email, "LOGIN_FAILED", "Usuário inexistente | IP: " + ip));
            throw new RuntimeException("Usuário não encontrado");
        });

        if (!passwordEncoder.matches(password, user.getPassword())){

            securityLogRepository.save(new SecurityLog(email, "LOGIN_FAILED", "Senha inválida | IP: " + ip));
            throw new RuntimeException("Senha Inválida");
        }

        if (!user.getVerified()) {
            String newCode = generateCode();
            user.setVerificationCode(passwordEncoder.encode(newCode));
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);

            emailService.sendVerificationEmail(user.getEmail(), newCode);


            securityLogRepository.save(new SecurityLog(email, "LOGIN_BLOCKED", "Usuário não verificado, novo código enviado | IP: " + ip));
            throw new RuntimeException("USER_NOT_VERIFIED");
        }


        securityLogRepository.save(new SecurityLog(email, "LOGIN_SUCCESS", "Acesso permitido | IP: " + ip));

        return jwtService.generateToken(user);
    }


    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            long minutesLeft = java.time.Duration.between(LocalDateTime.now(), user.getLockTime()).toMinutes();
            throw new RuntimeException("Muitas tentativas. Bloqueado por mais " + (minutesLeft + 1) + " minutos.");
        }


        if (user.getVerificationCodeExpiresAt() == null || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código expirado ou inexistente.");
        }


        if (passwordEncoder.matches(code, user.getVerificationCode())) {

            user.setVerified(true);
            user.setVerificationCode(null);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        } else {
            int currentAttempts = (user.getFailedAttempts() == null) ? 0 : user.getFailedAttempts();
            int attempts = currentAttempts + 1;

            user.setFailedAttempts(attempts);

            if (attempts >= 5) {
                user.setLockTime(LocalDateTime.now().plusMinutes(10));
                userRepository.save(user);
                throw new RuntimeException("Limite de tentativas excedido. Bloqueado por 10 minutos.");
            }

            userRepository.save(user);
            throw new RuntimeException("Código inválido! Tentativa " + attempts + " de 5.");
        }
    }
    public void resendVerificationCode(String email, HttpServletRequest request) { // Adicionado request
        String ip = request.getRemoteAddr();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    securityLogRepository.save(new SecurityLog(email, "RESEND_FAILED", "Tentativa de reenvio para e-mail inexistente | IP: " + ip));
                    return new RuntimeException("Usuário não encontrado");
                });

        if(user.getVerified()){
            securityLogRepository.save(new SecurityLog(email, "RESEND_BLOCKED", "Tentativa de reenvio para conta já verificada | IP: " + ip));
            throw new RuntimeException("Usuário já verificado");
        }

        String codeResend = generateCode();
        user.setVerificationCode(passwordEncoder.encode(codeResend));
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), codeResend);

        securityLogRepository.save(new SecurityLog(email, "RESEND_SUCCESS", "Novo código enviado com sucesso | IP: " + ip));
    }


    public void processPasswordResetRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não cadastrado em nossa base."));

        String code = generateCode();
        user.setVerificationCode(passwordEncoder.encode(code));
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    public void resetPassword(String email, String newPassword, HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    securityLogRepository.save(new SecurityLog(email, "PASSWORD_RESET_FAILURE", "Usuário não encontrado | IP: " + ip));
                    return new RuntimeException("Usuário não encontrado");
                });


        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            String errorMsg = "A nova senha não pode ser igual à senha atual.";


            securityLogRepository.save(new SecurityLog(email, "PASSWORD_RESET_FAILURE", errorMsg + " | IP: " + ip));


            throw new RuntimeException(errorMsg);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationCode(null);
        userRepository.save(user);

        securityLogRepository.save(new SecurityLog(email, "PASSWORD_RESET_SUCCESS", "Senha alterada com sucesso | IP: " + ip));
    }}