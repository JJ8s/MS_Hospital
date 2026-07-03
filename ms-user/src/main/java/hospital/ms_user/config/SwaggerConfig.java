package hospital.ms_user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Usuarios - Hospital")
                        .version("1.0.0")
                        .description("Microservicio encargado del registro, búsqueda y eliminación de usuarios del sistema hospitalario.")
                        .contact(new Contact()
                                .name("Soporte IT Hospital")
                                .email("soporte@hospital.com")));
    }
}