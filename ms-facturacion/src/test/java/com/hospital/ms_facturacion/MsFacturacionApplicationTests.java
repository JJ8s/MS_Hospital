package com.hospital.ms_facturacion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MsFacturacionApplicationTests {

    @Test
    void mainClassExiste() {
        assertDoesNotThrow(() -> Class.forName("com.hospital.ms_facturacion.MsFacturacionApplication"));
    }
}
