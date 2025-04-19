package com.alamin.ecommerce.subscribe;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final JavaMailSender mailSender;

    public SubscribeService(SubscribeRepository subscribeRepository, JavaMailSender mailSender) {
        this.subscribeRepository = subscribeRepository;
        this.mailSender = mailSender;
    }

    public Subscribe subscribe(String email, String name){
        Subscribe subscribe = new Subscribe();
        subscribe.setEmail(email);
        subscribe.setName(name);
        subscribe.setStatus("pending");
        subscribe.setLastEmailDate(LocalDateTime.now());
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-","");
        subscribe.setId(uuid);
        String emailVerificationToken = generateEmailVerificationToken();
        subscribe.setEmailVerificationToken(emailVerificationToken);
        subscribe.setEmailVerificationTokenExpiration(LocalDateTime.now().plusDays(15));
        Subscribe save = subscribeRepository.save(subscribe);
        //todo:add send welcome email
        sendWelcomeEmail(email, name);
        //todo add verification email
        sendVerificationEmail(email, emailVerificationToken);
        return save;
    }

    private void sendVerificationEmail(String email, String uuid) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Email Verification");
            helper.setText("Please verify your email by clicking the link: " +
                    generateEmailVerificationLink(uuid));
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
        }
    }

    private void sendWelcomeEmail(String email, String name) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Welcome to Our Service");
            helper.setText("Dear " + name + ",\n\nThank you for subscribing to our service!\n\nBest regards,\nE-commerce Team");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
        }
    }

    private String generateEmailVerificationToken(){
        String token = UUID.randomUUID().toString();
        token = token.replace("-", "");
        return token;
    }

    private String generateEmailVerificationLink(String token){
        return "http://localhost:8080/verify?token=" + token;
    }

    public Subscribe verifyEmail(String token) {
        Subscribe subscribe = subscribeRepository.findById(token).orElse(null);
        if (subscribe != null) {
            subscribe.setStatus("verified");
            return subscribeRepository.save(subscribe);
        }
        return null;
    }
}
