package hospital.ms_auth.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
public class ErrorResponse {
    
    private int status;
    private String mensaje;
    
    private long timestamp = System.currentTimeMillis(); 

    
    public ErrorResponse(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
        
    }
}