package hospital.ms_user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> manejarBadRequest(BadRequestException ex) {
        // Usamos el molde del paso 1 y la alarma del paso 2
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(), 
            ex.getMessage()
    );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Este método atrapa errores de formato en el JSON (como comas mal puestas o nombres de campos erróneos)
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarErrorJson(org.springframework.http.converter.HttpMessageNotReadableException ex) {
         ErrorResponse error = new ErrorResponse(
             HttpStatus.BAD_REQUEST.value(),
            "Error en el formato del JSON: Revisa que los campos y comas estén correctos"
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // para que la creacion de roles y usuarios sean correctas
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> manejarErroresValidacion(org.springframework.web.bind.MethodArgumentNotValidException ex) {
    // Obtenemos el primer error de validación que encuentre
    String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), mensaje);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}
    
}
