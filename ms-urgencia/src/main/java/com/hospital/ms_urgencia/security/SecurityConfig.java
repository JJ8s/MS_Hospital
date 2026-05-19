package com.hospital.ms_urgencia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // GET
                .requestMatchers(HttpMethod.GET, "/api/urgencias/**")
                .permitAll()

                // POST
                .requestMatchers(HttpMethod.POST, "/api/urgencias/**")
                .permitAll()

                // PATCH
                .requestMatchers(HttpMethod.PATCH, "/api/urgencias/**")
                .permitAll()

                // DELETE
                .requestMatchers(HttpMethod.DELETE, "/api/urgencias/**")
                .permitAll()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}