package com.example.app.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConversorServiceTest {

    // Testando ParseDouble

    @Test
    @DisplayName("Teste de Conversão parseDouble - para valores válidos")
    void deveTentarParseDoubleValido() {

        assertEquals(12.34, ConversorService.parseDouble("12.34"));

    }

    @Test
    @DisplayName("Teste de Conversão parseDouble - para valores inválidos")
    void deveTentarParseDoubleInvalido() {

        assertNull(ConversorService.parseDouble("abc"));
        assertNull(ConversorService.parseDouble(null));

    }

    // Testando ParseInt

    @Test
    @DisplayName("Teste de Conversão parseInt - para valores válidos")
    void deveTentarParseIntValido() {

        assertEquals(123, ConversorService.parseInt("123"));

    }

    @Test
    @DisplayName("Teste de Conversão parseInt - para valores inválidos")
    void deveTentarParseIntInvalido() {

        assertNull(ConversorService.parseInt("12.3"));
        assertNull(ConversorService.parseInt("x"));
        assertNull(ConversorService.parseInt(null));

    }

    // Testando feetToMeters ( pés para metros )

    @Test
    @DisplayName("Teste de Conversão feetToMeters - para valores válidos")
    void deveTentarFeetToMetersValido() {

        assertEquals(305, ConversorService.feetToMeters("1000")); // 1000 * 0.3048 = 304.8 ~ 305

    }

    @Test
    @DisplayName("Teste de Conversão feetToMeters - para valores inválidos")
    void deveTentarFeetToMetersInvalido() {

        assertNull(ConversorService.feetToMeters("abc"));
        assertNull(ConversorService.feetToMeters(null));

    }
}
