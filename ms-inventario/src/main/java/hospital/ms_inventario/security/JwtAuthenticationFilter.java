package hospital.ms_inventario.security;
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

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET ="12345678901234567890123456789012";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            try {

                String token = header.substring(7);

                SecretKey key = Keys.hmacShaKeyFor(
                        SECRET.getBytes(StandardCharsets.UTF_8)
                );

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
                        .map(role -> {

                            role = role.trim();

                            if (!role.startsWith("ROLE_")) {
                                role = "ROLE_" + role;
                            }

                            return new SimpleGrantedAuthority(role);

                        })
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {

                SecurityContextHolder.clearContext();

            }
        }

        chain.doFilter(request, response);
    }
}