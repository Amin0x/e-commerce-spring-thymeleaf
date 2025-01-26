package com.alamin.ecommerce.user;

import jakarta.persistence.Column;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserForm extends SignupForm{
    private String role;
    private String email;
    private String avatar;
    private Boolean enabled;
    private String status;
    private String firstName;
    private String lastName;
    private int day;
    private int month;
    private int year;
    private MultipartFile image;
    private String address;
    private String city;
    private String phone;
    private String state;
    private String country;

}
