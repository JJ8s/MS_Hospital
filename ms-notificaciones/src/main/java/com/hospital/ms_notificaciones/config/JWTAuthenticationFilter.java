package com.hospital.ms_notificaciones.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;

    public JWTAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        extraerAuthorities(claims)
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private Collection<SimpleGrantedAuthority> extraerAuthorities(Claims claims) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        agregarClaimComoAuthority(authorities, claims.get("roles"));
        agregarClaimComoAuthority(authorities, claims.get("role"));
        agregarClaimComoAuthority(authorities, claims.get("rol"));
        agregarClaimComoAuthority(authorities, claims.get("authorities"));
        return authorities;
    }

    private void agregarClaimComoAuthority(List<SimpleGrantedAuthority> authorities, Object claim) {
        if (claim instanceof Collection<?> collection) {
            for (Object value : collection) {
                agregarAuthority(authorities, String.valueOf(value));
            }
            return;
        }

        if (claim != null) {
            agregarAuthority(authorities, String.valueOf(claim));
        }
    }

    private void agregarAuthority(List<SimpleGrantedAuthority> authorities, String role) {
        String cleanRole = role.replace("ROLE_", "").trim();
        if (!cleanRole.isBlank()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + cleanRole));
        }
    }
}
