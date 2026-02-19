package com.duarte.studyflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Studyflow - Verificação de email");
        message.setText(
                "Seu código de verificação é:\n\n" +
                        code + "\n\nDigite esse código para confirmar seu email.");

        mailSender.send(message);
    }
}
