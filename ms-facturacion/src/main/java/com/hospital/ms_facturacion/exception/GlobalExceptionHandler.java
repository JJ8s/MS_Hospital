package com.hospital.ms_facturacion.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(FacturaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleConflict(FacturaDuplicadaException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.CONFLICT, "Conflicto de datos", ex.getMessage(), request);
    }

    @ExceptionHandler(IntegracionFacturacionException.class)
    public ResponseEntity<ErrorResponse> handleIntegracion(IntegracionFacturacionException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.BAD_GATEWAY, "Error de integracion", ex.getMessage(), request);
    }

    @ExceptionHandler({EstadoFacturaException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        String mensaje = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException validationException) {
            mensaje = validationException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Solicitud invalida", mensaje, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(
            HttpStatus status,
            String error,
            String mensaje,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                mensaje,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, status);
    }
}
