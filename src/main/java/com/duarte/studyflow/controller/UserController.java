package com.duarte.studyflow.controller;

import com.duarte.studyflow.model.User;
import com.duarte.studyflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok().body(savedUser);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
