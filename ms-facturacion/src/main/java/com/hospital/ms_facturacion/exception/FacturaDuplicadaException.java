package com.hospital.ms_facturacion.exception;

public class FacturaDuplicadaException extends RuntimeException {
    public FacturaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
