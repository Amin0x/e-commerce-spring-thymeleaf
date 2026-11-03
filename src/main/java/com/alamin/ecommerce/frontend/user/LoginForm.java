package com.alamin.ecommerce.frontend.user;

/**
 * Backing object for the login form posted to {@code POST /auth/login}.
 */
public class LoginForm {

    private String username;
    private String password;

    public LoginForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
