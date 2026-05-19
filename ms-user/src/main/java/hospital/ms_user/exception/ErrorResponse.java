package hospital.ms_user.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String mensaje;
    private long timestamp = System.currentTimeMillis();

    // Este es el constructor que usaremos en el Handler
    public ErrorResponse(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = System.currentTimeMillis();
    }
}
    
    // Genera sus Getters y Setters

