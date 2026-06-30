package com.hospital.ms_inventario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MsInventarioApplicationTests {

    @Test
    void mainClassExiste() {
        assertDoesNotThrow(() -> Class.forName("com.hospital.ms_inventario.MsInventarioApplication"));
    }
}
