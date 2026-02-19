package com.duarte.studyflow.controller;

import com.duarte.studyflow.dto.VerifyRequest;
import com.duarte.studyflow.model.User;
import com.duarte.studyflow.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return ResponseEntity.ok(
                authService.login(user.getEmail(), user.getPassword())
        );
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
        authService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Email verificado com sucesso");
    }
    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestParam String email) {
        authService.resendVerificationCode(email);
        return ResponseEntity.ok("Novo código enviado para o email");
    }
}
