package hospital.ms_user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Usuarios - Hospital")
                        .version("1.0.0")
                        .description("""
                                Microservicio encargado del registro, búsqueda y desactivación \
                                (borrado lógico) de usuarios del sistema hospitalario.

                                ### Notas rápidas
                                - Los correos (`autoEmail`) se generan automáticamente a partir del nombre y apellido.
                                - Las contraseñas se almacenan encriptadas con BCrypt, nunca en texto plano.
                                - El borrado es lógico: los usuarios se desactivan (`active=false`), nunca se eliminan de la BD.
                                """)
                        .termsOfService("https://hospital.com/terminos")
                        .contact(new Contact()
                                .name("Soporte IT Hospital")
                                .email("soporte@hospital.com")
                                .url("https://hospital.com/soporte"))
                        .license(new License()
                                .name("Uso interno - Hospital")
                                .url("https://hospital.com/licencia")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación completa del ecosistema de microservicios")
                        .url("https://hospital.com/docs"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Vía API Gateway (recomendado)"),
                        new Server().url("http://localhost:8081").description("Directo al microservicio (desarrollo)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese su Token JWT en formato: Bearer {tu_token}")));
    }
}