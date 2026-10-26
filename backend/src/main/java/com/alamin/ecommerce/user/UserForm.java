package com.alamin.ecommerce.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserForm {
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
