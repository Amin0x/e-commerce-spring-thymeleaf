package com.alamin.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.thymeleaf.messageresolver.AbstractMessageResolver;


@Configuration(proxyBeanMethods = false)
//@EnableJdbcHttpSession 
public class Config {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public LocaleResolver localResolver() {
        return new CookieLocaleResolver();
    }

}
