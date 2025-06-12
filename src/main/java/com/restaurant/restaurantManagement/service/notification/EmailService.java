package com.restaurant.restaurantManagement.service.notification;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String destiny, String subject, String text) {
        var message = new SimpleMailMessage();

        message.setTo(destiny);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("noreply@restaurant.com");
        mailSender.send(message);
    }
}
