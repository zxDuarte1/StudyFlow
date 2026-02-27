package com.duarte.studyflow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_logs")
@Getter
@Setter
@NoArgsConstructor
public class SecurityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String action;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;

    public SecurityLog(String email, String action, String details) {
        this.email = email;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}