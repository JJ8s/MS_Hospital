package com.hospital.pacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;


@RestController
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundExceptione.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundExceptione ex){
        Map<String, String> error= new HashMap<>();
        error.put("error", "no encontrado");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(DuplicadoException.class)
    public ResponseEntity<Map<String, String>> handleDuplicado(DuplicadoException ex){
        Map<String, String> error= new HashMap<>();
        error.put("error", "Conflicto");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error->{
            String campo = ((FieldError)error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);

        });
        return ResponseEntity.badRequest().body(errores);
    
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex){
        Map<String, String> error = new HashMap<>();
        error.put("error","error interno del servidor");
        error.put("mensaje", "Contacte con el administrador");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
