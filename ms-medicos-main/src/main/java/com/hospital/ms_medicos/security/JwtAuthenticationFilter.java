package com.hospital.ms_medicos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
    // 1. CLAVE COMPARTIDA CON MS-AUTH: Debe medir mínimo 32 caracteres (256 bits)
    private static final String SECRET = "12345678901234567890123456789012";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 2. ESTÁNDAR HTTP: Cambiado de "Autorizado" al encabezado global "Authorization"
        String header = request.getHeader("Authorization");
        
        // 3. ESPACIO EN BEARER: Aseguramos el espacio reglamentario después de 'Bearer'
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
                
                // --- INICIO DEL NUEVO BLOQUE FLEXIBLE (SECCIONES 4 Y 5) ---
                
                // 4. LECTURA FLEXIBLE: Soporta Listas o Strings simples sin romperse si ms-auth cambia el formato
                Object rolesObject = claims.get("roles");
                List<String> roles = new ArrayList<>();

                if (rolesObject instanceof List) {
                    roles = (List<String>) rolesObject;
                } else if (rolesObject instanceof String) {
                    roles.add((String) rolesObject);
                }
                
                // 5. NORMALIZACIÓN DE ROLES: Aseguramos el prefijo "ROLE_" que exige hasAnyRole() limpiando espacios en blanco
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(rol -> {
                            rol = rol.trim(); 
                            if (!rol.startsWith("ROLE_")) {
                                rol = "ROLE_" + rol;
                            }
                            return new SimpleGrantedAuthority(rol);
                        })
                        .collect(Collectors.toList());
                
                // --- FIN DEL BLOQUE FLEXIBLE ---
                
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                // Autorizamos la petición en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(auth);
                
            } catch (Exception e) {
                // Si el token expira o la firma es inválida, limpiamos el contexto por seguridad
                SecurityContextHolder.clearContext();
            }
        }
        
        // Continuar con el siguiente filtro en la cadena de Spring Security
        chain.doFilter(request, response);
    }
}
