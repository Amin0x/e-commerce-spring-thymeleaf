package com.alamin.ecommerce.config;

import java.util.Optional;

import com.alamin.ecommerce.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alamin.ecommerce.user.User;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userService;

    public CustomUserDetailsService(UserRepository userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userByUsername = userService.getUserByUsername(username);
        if (userByUsername.isEmpty()) {
            log.error("User not found with username: {}", username);
            throw new UsernameNotFoundException("");
        }

        log.info("User found with username: {}", username);
        log.info("User found with user: {}", userByUsername.get());
        return userByUsername.get();

    }

}
