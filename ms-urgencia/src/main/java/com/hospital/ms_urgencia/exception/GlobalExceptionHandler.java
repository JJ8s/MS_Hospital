package com.hospital.ms_urgencia.exception;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Hidden
    @ExceptionHandler(ResourceNotFoundExceptione.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundExceptione ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @Hidden
    @ExceptionHandler(NivelTriageInvalidoException.class)
    public ResponseEntity<ApiError> handleNivelInvalido(NivelTriageInvalidoException ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "NIVEL_TRIAGE_INVALIDO", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @Hidden
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiError> handleFeignNotFound(FeignException.NotFound ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "RECURSO_EXTERNO_NO_ENCONTRADO", "El paciente referenciado no existe en el sistema");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @Hidden
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ApiError> handleFeignServiceUnavailable(FeignException.ServiceUnavailable ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_GATEWAY.value(), "SERVICIO_NO_DISPONIBLE", "El servicio de pacientes no está disponible");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @Hidden
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeignGeneral(FeignException ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_GATEWAY.value(), "ERROR_COMUNICACION_SERVICIO_EXTERNO", "Ocurrió un error al consultar el servicio de pacientes");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @Hidden
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        return ResponseEntity.badRequest().body(errores);
    }

    @Hidden
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}