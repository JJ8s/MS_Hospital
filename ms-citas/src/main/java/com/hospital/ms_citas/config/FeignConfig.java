package com.hospital.ms_citas.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(HttpServletRequest request) {
        return template -> {

            String authorization = request.getHeader("Authorization");

            if (authorization != null) {
                template.header("Authorization", authorization);
            }
        };
    }
}