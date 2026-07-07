package com.hospital.ms_inventario.exception;

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

    @ExceptionHandler({ProductoDuplicadoException.class, LoteDuplicadoException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.CONFLICT, "Conflicto de datos", ex.getMessage(), request);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, "Error de stock", ex.getMessage(), request);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
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
