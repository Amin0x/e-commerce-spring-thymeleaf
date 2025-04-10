package com.alamin.ecommerce.user;

import lombok.Data;

@Data
public class SignupForm {
    private String email;
    private String password;
    private String confirmPassword;
}
