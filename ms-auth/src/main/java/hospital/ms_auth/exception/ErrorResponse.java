package hospital.ms_auth.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Genera el constructor público sin argumentos necesario para Jackson (JSON)
public class ErrorResponse {
    
    private int status;
    private String mensaje;
    // Se inicializa automáticamente con la hora exacta del servidor en milisegundos al instanciarse
    private long timestamp = System.currentTimeMillis(); 

    // Constructor manual personalizado para tu GlobalExceptionHandler
    public ErrorResponse(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
        // No hace falta setear el timestamp aquí, ya se ejecuta arriba al crear el objeto.
    }
}