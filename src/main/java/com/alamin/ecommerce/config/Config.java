package com.alamin.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration(proxyBeanMethods = false)
//@EnableJdbcHttpSession 
public class Config {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
