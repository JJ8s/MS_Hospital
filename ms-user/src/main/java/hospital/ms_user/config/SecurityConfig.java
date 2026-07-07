package hospital.ms_user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Este Bean se encargará de encriptar la contraseña antes de guardarla en XAMPP
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Filtro para configurar qué rutas son públicas y cuáles protegidas
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos CSRF ya que las APIs REST usan tokens (JWT) y no cookies
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permitimos el registro/consulta de usuarios y rutas de Swagger abiertamente en desarrollo
                        .requestMatchers("/api/users/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // Cualquier otra petición requerirá estar autenticado
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}