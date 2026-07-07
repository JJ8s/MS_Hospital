package hospital.ms_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token emitido tras una autenticación exitosa")
public class AuthResponseDTO {

    @Schema(description = "Token JWT firmado, válido por 1 hora",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWEucGVyQGhvc3BpdGFsLmNvbSIsInJvbGVzIjpbIlBBQ0lFTlRFIl0sImlhdCI6MTcyMDAwMDAwMCwiZXhwIjoxNzIwMDAzNjAwfQ.example-signature")
    private String token;

    @Schema(description = "Tipo de token, siempre 'Bearer'", example = "Bearer")
    private String type = "Bearer";

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}