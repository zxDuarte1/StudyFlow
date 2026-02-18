package com.duarte.studyflow.controller;

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
}
