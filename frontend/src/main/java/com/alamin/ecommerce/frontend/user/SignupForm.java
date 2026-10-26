package com.alamin.ecommerce.frontend.user;

/**
 * Backing object for the signup form posted to {@code POST /auth/register}.
 */
public class SignupForm {

    private String username;
    private String password;
    private String email;

    public SignupForm() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
