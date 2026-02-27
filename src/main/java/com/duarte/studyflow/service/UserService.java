package com.duarte.studyflow.service;

import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,EmailService emailService) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.emailService = emailService;
        }

    public User createUser(User user) {

        if (user.getName() != null) {
            user.setName(Jsoup.clean(user.getName(), Safelist.none()));
        }
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }

        validatePasswordStrength(user.getPassword());

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("Email já cadastrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setVerificationCode(passwordEncoder.encode(code));

        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setVerified(false);

        emailService.sendVerificationEmail(user.getEmail(), code);
        return userRepository.save(user);
    }
    private void validatePasswordStrength(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!password.matches(regex)) {
            throw new RuntimeException("A senha deve ter pelo menos 8 caracteres, " +
                    "incluindo uma letra maiúscula, um número e um caractere especial.");
        }
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
