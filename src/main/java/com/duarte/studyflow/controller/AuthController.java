package com.duarte.studyflow.controller;

import com.duarte.studyflow.dto.ResetPasswordDTO;
import com.duarte.studyflow.dto.VerifyRequest;
import com.duarte.studyflow.model.User;
import com.duarte.studyflow.repository.UserRepository;
import com.duarte.studyflow.service.AuthService;
import com.duarte.studyflow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    public AuthController(AuthService authService, UserService userService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        try {

            String token = authService.login(user.getEmail(), user.getPassword(), request);
            User userFull = userRepository.findByEmail(user.getEmail()).get();


            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("nome", userFull.getName());
            response.put("email", userFull.getEmail());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());


            int status = e.getMessage().equals("USER_NOT_VERIFIED") ? 403 : 401;
            return ResponseEntity.status(status).body(errorResponse);
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
        try {
            authService.verifyEmail(request.getEmail(), request.getCode());
            return ResponseEntity.ok("Email verificado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/forgot-password-request")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            authService.processPasswordResetRequest(email);
            return ResponseEntity.ok("Se o e-mail existir, o código foi enviado.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestParam String email, HttpServletRequest request) {
        try {
            authService.resendVerificationCode(email, request);
            return ResponseEntity.ok("Código reenviado!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO data,HttpServletRequest request) {
        try {
            authService.resetPassword(data.getEmail(), data.getNewPassword(), request);
            return ResponseEntity.ok("Senha alterada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}