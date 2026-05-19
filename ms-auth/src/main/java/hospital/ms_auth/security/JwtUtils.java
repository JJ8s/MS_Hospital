package hospital.ms_auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    // LA MISMA QUE EN MS-MEDICOS (32 caracteres)
    private final String SECRET_KEY = "12345678901234567890123456789012";

    public String createToken(String username, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // Aquí guardas los roles que luego leerá ms-medicos
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(key)
                .compact();
    }

    
}
