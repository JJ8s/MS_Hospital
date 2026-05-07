package com.hospital.ms_citas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundExceptione extends RuntimeException{
    public ResourceNotFoundExceptione(String message){
        super(message);
    }
}
