package com.hospital.ms_notificaciones;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MsNotificacionesApplicationTests {

    @Test
    void mainClassExiste() {
        assertDoesNotThrow(() -> Class.forName("com.hospital.ms_notificaciones.MsNotificacionesApplication"));
    }
}
