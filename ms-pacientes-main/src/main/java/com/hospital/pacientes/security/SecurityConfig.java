package com.hospital.pacientes.security;

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
    public JwtAuthenticationFilter jwFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger: rutas completas incluyendo /v3/api-docs raíz
                .requestMatchers(
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers(HttpMethod.POST, "/api/pacientes/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/pacientes/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
