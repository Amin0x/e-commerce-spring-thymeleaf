package com.alamin.ecommerce.subscribe;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscribeService {
    public static final String STATUS_SUBSCRIBED = "SUBSCRIBED";
    public static final String STATUS_UNSUBSCRIBED = "UNSUBSCRIBED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_DELETED = "DELETED";

    private final SubscribeRepository subscribeRepository;
    private final JavaMailSender mailSender;

    public SubscribeService(SubscribeRepository subscribeRepository, JavaMailSender mailSender) {
        this.subscribeRepository = subscribeRepository;
        this.mailSender = mailSender;
    }

    public Subscribe subscribe(String email, String name){
        Optional<Subscribe> subscribeOptional = subscribeRepository.findByEmail(email);
        String token = createEmailVerificationToken();
        LocalDateTime now = LocalDateTime.now();

        if (subscribeOptional.isPresent()){
            Subscribe subscribe = subscribeOptional.get();
            if (subscribe.getStatus().equals(STATUS_SUBSCRIBED)){
                throw new RuntimeException("you are already subscribed");
            }
            if (subscribe.getStatus().equals(STATUS_PENDING)){
                subscribe.setVerificationToken(token);
                subscribe.setTokenExpiration(now.plusDays(15));
                return subscribeRepository.save(subscribe);
            }
            subscribe.setStatus(STATUS_PENDING);
            subscribe.setVerificationToken(token);
            subscribe.setTokenExpiration(now.plusDays(15));
            return subscribeRepository.save(subscribe);
        }

        Subscribe subscribe = new Subscribe(email, name, STATUS_PENDING, token, now.plusDays(15), now, now);
        Subscribe save = subscribeRepository.save(subscribe);

        sendWelcomeEmail(email, name);
        sendVerificationEmail(email, token);

        return save;
    }

    public void unsubscribe(String email){
        Subscribe subscribe = subscribeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("you are not subscribed"));
        if (subscribe.getStatus().equals(STATUS_UNSUBSCRIBED)){
            throw new RuntimeException("you are already unsubscribed");
        }
        if (subscribe.getStatus().equals(STATUS_DELETED)){
            throw new RuntimeException("you are already deleted");
        }
        subscribe.setStatus(STATUS_UNSUBSCRIBED);
        subscribeRepository.save(subscribe);
    }

    private void sendVerificationEmail(String email, String uuid) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Email Verification");
            String verificationLink = createEmailVerificationLink(uuid, email);
            helper.setText("Please verify your email by clicking the link: " +
                    verificationLink);
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

    private String createEmailVerificationToken(){
        String token = UUID.randomUUID().toString();
        token = token.replace("-", "");
        return token;
    }

    private String createEmailVerificationLink(String token, String email){
        return "http://localhost:8080/verify?token=" + token +"&email=" + email;
    }

    public Subscribe verifyEmail(String email, String token) {
        Subscribe subscribe = subscribeRepository.findByEmail(email).orElse(null);
        if (subscribe == null) {
            throw new RuntimeException("Email not found");
        }

        if(!Objects.equals(token, subscribe.getVerificationToken())){
            throw new RuntimeException("invalid token");
        }

        if (LocalDateTime.now().isAfter(subscribe.getTokenExpiration())){
            throw new RuntimeException("invalid token");
        }
        subscribe.setStatus(STATUS_SUBSCRIBED);
        return subscribeRepository.save(subscribe);
    }
}
