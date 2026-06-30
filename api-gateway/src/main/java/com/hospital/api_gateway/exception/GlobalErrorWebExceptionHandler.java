package com.hospital.api_gateway.exception;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
@Order(-2) 
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Error interno en el servidor Gateway.";

        
        if (ex instanceof org.springframework.web.reactive.resource.NoResourceFoundException 
            || (ex instanceof ResponseStatusException && ((ResponseStatusException) ex).getStatusCode() == HttpStatus.NOT_FOUND)) {
            status = HttpStatus.NOT_FOUND;
            message = "La ruta o endpoint solicitado no existe en el sistema.";
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

       
        String jsonResponse = String.format(
                "{\n  \"timestamp\": \"%s\",\n  \"status\": %d,\n  \"error\": \"%s\",\n  \"message\": \"%s\",\n  \"path\": \"%s\"\n}",
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value()
        );

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}