package com.campussync.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = "https://campussync.com/verify?token=" + token;
        System.out.println("==================================================");
        System.out.println("Sending Verification Email to: " + toEmail);
        System.out.println("Subject: Verify your CampusSync account");
        System.out.println("Body: Click here to verify your email: " + verificationUrl);
        System.out.println("==================================================");
    }
}
