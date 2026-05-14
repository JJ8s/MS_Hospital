package hospital.ms_facturacion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. errores de validación 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String mensajeError = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de Datos de Facturación",
                mensajeError,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 2. errores de lógica 
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        HttpStatus status = ex.getMessage().contains("no encontrada") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status == HttpStatus.NOT_FOUND ? "Factura No Encontrada" : "Error en Proceso de Cobro",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, status);
    }

    // 3. errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Crítico Financiero",
                "No se pudo procesar la transacción bancaria interna.",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
