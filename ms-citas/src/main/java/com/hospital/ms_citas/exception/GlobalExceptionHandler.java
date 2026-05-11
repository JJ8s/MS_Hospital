package com.hospital.ms_citas.exception;
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
        Map<String, String> error = new HashMap<>();
        error.put("error", "No encontrado");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(HorarioNoDisponibleException.class)
    public ResponseEntity<Map<String, String>> handleHorarioNoDisponibleException(HorarioNoDisponibleException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflicto");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }    
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleFeignExceptionNotFound(FeignException.NotFound ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Recurso externo no encontrado");
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<Map<String, String>> FeignExceptionServiceUnavailable(FeignException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", "Servicio no disponible");
        error.put("error", "Ocurrio un error al consultar otro microservicio");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>MethodArgumentNotValid(MethodArgumentNotValidException ex){
        Map<String, String>errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error->{
            String campo =((FieldError)error).getField();
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

