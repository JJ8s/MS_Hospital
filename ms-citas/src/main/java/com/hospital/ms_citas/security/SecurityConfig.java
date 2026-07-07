package com.hospital.ms_citas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 🌟 MENSAJE DE ERROR PERSONALIZADO EN JSON CUANDO FALTA EL TOKEN O ES INVÁLIDO
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
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/citas", "/api/citas/**")
                        .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")

                        .requestMatchers(HttpMethod.POST, "/api/citas", "/api/citas/**")
                        .hasAnyRole("ADMIN", "PACIENTE")

                        .requestMatchers(HttpMethod.PUT, "/api/citas", "/api/citas/**")
                        .hasAnyRole("ADMIN", "MEDICO")

                        .requestMatchers(HttpMethod.PATCH, "/api/citas", "/api/citas/**")
                        .hasAnyRole("ADMIN", "MEDICO")

                        .requestMatchers(HttpMethod.DELETE, "/api/citas", "/api/citas/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new OncePerRequestFilter() {
            private static final String SECRET = "12345678901234567890123456789012";

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                    throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                if (header != null && header.startsWith("Bearer ")) {
                    try {
                        String token = header.substring(7);
                        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

                        Claims claims = Jwts.parser()
                                .verifyWith(key)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();

                        String username = claims.getSubject();

                        Object rolesObject = claims.get("roles");
                        List<String> roles = new ArrayList<>();

                        if (rolesObject instanceof List) {
                            roles = (List<String>) rolesObject;
                        } else if (rolesObject instanceof String) {
                            roles.add((String) rolesObject);
                        }

                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(rol -> {
                                    rol = rol.trim();
                                    if (!rol.startsWith("ROLE_")) {
                                        rol = "ROLE_" + rol;
                                    }
                                    return new SimpleGrantedAuthority(rol);
                                })
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(auth);

                    } catch (Exception e) {
                        SecurityContextHolder.clearContext();
                    }
                }
                chain.doFilter(request, response);
            }
        };
    }
}