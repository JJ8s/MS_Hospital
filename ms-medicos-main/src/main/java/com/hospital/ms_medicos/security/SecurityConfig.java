package com.hospital.ms_medicos.security;

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
    public JwtAuthenticationFilter jwFilter(){
        return new JwtAuthenticationFilter();
    
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf->csrf.disable())
            .sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth->auth
                .requestMatchers("v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medcios/**").hasAnyRole("ADMIN","MEDICO")
                .requestMatchers(HttpMethod.POST, "/api/medicos").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/medicos/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/medicos/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwFilter(), UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }

}
