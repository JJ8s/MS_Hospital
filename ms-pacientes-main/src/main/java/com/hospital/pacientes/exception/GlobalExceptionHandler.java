package com.hospital.pacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;


@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundExceptione.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundExceptione ex){
        Map<String, String> error= new HashMap<>();
        error.put("error", "no encontrado");
        error.put("mensaje",ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobal(Exception ex){
        Map<String, String> error= new HashMap<>();
        error.put("error","error intern");
        error.put("mensaje",ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
