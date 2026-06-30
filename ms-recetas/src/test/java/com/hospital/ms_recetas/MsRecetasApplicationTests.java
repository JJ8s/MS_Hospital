package com.hospital.ms_recetas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MsRecetasApplicationTests {

    @Test
    void mainClassExiste() {
        assertDoesNotThrow(() -> Class.forName("com.hospital.ms_recetas.MsRecetasApplication"));
    }
}
