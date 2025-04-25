package com.alamin.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // Security configuration goes here
    // This is a placeholder for the actual security configuration code.
    // You can use Spring Security to configure authentication and authorization.
    
    // Example: configuring HTTP security, authentication manager, etc.

    private final CustomUserDetailsService userDetailsService;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/private/**").authenticated()
                .requestMatchers("/admin/**","/api/admin").hasAuthority("ADMIN")
                .requestMatchers("/user/**").hasAuthority("USER")
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated();
        })
        .csrf(Customizer.withDefaults())
        .formLogin((form)-> {
             form.loginPage("/auth/login").permitAll()
             /* .loginProcessingUrl("/auth/login") */
             /*.usernameParameter("username")
             .passwordParameter("password")*/;
        });
        
        return http.build();
    }
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Replace with your preferred password encoder
    }

    // defining a custom authentication manager
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }



    // Example: defining a custom authentication provider
    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    //     DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    //     provider.setUserDetailsService(userDetailsService());
    //     provider.setPasswordEncoder(new BCryptPasswordEncoder());
    //     return provider;
    // }
    // Example: defining a custom user details service

    // Example: defining a custom authentication filter
    // @Bean
    // public AuthenticationFilter authenticationFilter() {
    //     return new CustomAuthenticationFilter(); // Replace with your custom implementation
    // }
    // Example: defining a custom authentication entry point
    // @Bean
    // public AuthenticationEntryPoint authenticationEntryPoint() {
    //     return new CustomAuthenticationEntryPoint(); // Replace with your custom implementation
    // }
    // Example: defining a custom access denied handler
    // @Bean
    // public AccessDeniedHandler accessDeniedHandler() {
    //     return new CustomAccessDeniedHandler(); // Replace with your custom implementation
    // }
    // Example: defining a custom session management strategy
    // @Bean
    // public SessionManagementFilter sessionManagementFilter() {
    //     return new CustomSessionManagementFilter(); // Replace with your custom implementation
    // }
    // Example: defining a custom CORS configuration
    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/api/**")
    //                 .allowedOrigins("http://localhost:3000")
    //                 .allowedMethods("GET", "POST", "PUT", "DELETE")
    //                 .allowedHeaders("*")
    //                 .allowCredentials(true);
    //         }
    //     };
    // }
    // Example: defining a custom filter chain for API requests
    // @Bean
    // public FilterChainProxy filterChainProxy() {
    //     List<SecurityFilterChain> filterChains = new ArrayList<>();
    //     filterChains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/api/**"), new ApiAuthenticationFilter()));
    //     filterChains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/admin/**"), new AdminAuthenticationFilter()));
    //     return new FilterChainProxy(filterChains);
    // }
    // Example: defining a custom authentication success handler
    // @Bean
    // public AuthenticationSuccessHandler authenticationSuccessHandler() {
    //     return new CustomAuthenticationSuccessHandler(); // Replace with your custom implementation
    // }
    // Example: defining a custom authentication failure handler
    // @Bean
    // public AuthenticationFailureHandler authenticationFailureHandler() {
    //     return new CustomAuthenticationFailureHandler(); // Replace with your custom implementation
    // }
    // Example: defining a custom remember-me services
    // @Bean
    // public RememberMeServices rememberMeServices() {
    //     return new CustomRememberMeServices(); // Replace with your custom implementation
    // }
    // Example: defining a custom session registry
    // @Bean
    // public SessionRegistry sessionRegistry() {
    //     return new CustomSessionRegistry(); // Replace with your custom implementation
    // }
    // Example: defining a custom session management filter
    // @Bean
    // public SessionManagementFilter sessionManagementFilter() {
    //     return new CustomSessionManagementFilter(); // Replace with your custom implementation
    // }
    // Example: defining a custom security context repository
    // @Bean
    // public SecurityContextRepository securityContextRepository() {
    //     return new CustomSecurityContextRepository(); // Replace with your custom implementation
    // }



}
