package com.hospital.ms_urgencia.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundExceptione.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundExceptione ex){
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(NivelTriageInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleNivelInvalido(NivelTriageInvalidoException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error","Nivel de triage invalido");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleFeignNotFound(FeignException.NotFound ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Recurso externo no encontrado");
        error.put("mensaje", "El paciente referenciado no existe ");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<Map<String, String>> handleFeignServiceUnavailable(FeignException.ServiceUnavailable ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error","Servicio no dispobile");
        error.put("mensaje", "El servicio de pacientes no esta dispobile");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignGeneral(FeignException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error de comunicacion con servicio externo");
        error.put("mensaje", "Ocurrio un error al consultar pacientes");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error->{
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        return ResponseEntity.badRequest().body(errores);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("mensaje", "Contacte con el administrador");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}


