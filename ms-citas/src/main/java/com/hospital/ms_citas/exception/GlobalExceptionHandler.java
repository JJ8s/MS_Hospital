package com.hospital.ms_citas.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Hidden en cada @ExceptionHandler evita que springdoc-openapi intente
    // construir las "generic error responses" a partir de este ControllerAdvice.
    // Sin esto, en Spring Boot 3.5.x (Spring Framework 6.2+) springdoc lanza:
    // NoSuchMethodError: ControllerAdviceBean.<init>(Object)
    // porque ese constructor fue eliminado del framework.
    // Ver: https://github.com/springdoc/springdoc-openapi/issues/3041

    @Hidden
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> manejarNoEncontrado(ResourceNotFoundException ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @Hidden
    @ExceptionHandler(HorarioNoDisponibleException.class)
    public ResponseEntity<ApiError> manejarHorarioNoDisponible(HorarioNoDisponibleException ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "CONFLICT", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @Hidden
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiError> manejarFeignNotFound(FeignException.NotFound ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "RECURSO_EXTERNO_NO_ENCONTRADO", "El médico o paciente indicado no existe en el sistema");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @Hidden
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ApiError> manejarFeignServiceUnavailable(FeignException.ServiceUnavailable ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_GATEWAY.value(), "SERVICIO_NO_DISPONIBLE", "Ocurrió un error al consultar otro microservicio");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @Hidden
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    @Hidden
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarGeneral(Exception ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}