package com.hospital.pacientes.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🌟 MANEJOR DE EXCEPCIONES: Devuelve un JSON limpio en lugar del error genérico de Tomcat
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Devuelve 403 Forbidden
                            response.getWriter().write("{"
                                    + "\"error\": \"No autorizado\","
                                    + "\"message\": \"Token JWT inválido, expirado o ausente. Debes proporcionar un token válido en el encabezado Authorization.\""
                                    + "}");
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // Swagger y OpenAPI libres de restricciones
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Restricciones de accesos por roles configurados en tu endpoint
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole("ADMIN", "MEDICO")
                        .requestMatchers(HttpMethod.POST, "/api/pacientes/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // 🌟 Vinculamos correctamente el filtro inyectado como Bean en la cadena de Spring
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}