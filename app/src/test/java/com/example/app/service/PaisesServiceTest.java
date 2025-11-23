package com.example.app.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaisesServiceTest {

    @Test
    @DisplayName("Teste de Conversão de Nome em inglês para ISO2")
    void deveConverterNomeEmInglesParaIso2() {

        assertEquals("US", PaisesService.countryIso2("United States"));
        assertEquals("GB", PaisesService.countryIso2("United Kingdom"));

    }

    @Test
    @DisplayName("Teste de Conversão de ISO3 para ISO2")
    void deveConverterIso3ParaIso2() {

        assertEquals("US", PaisesService.countryIso2("USA"));
        assertEquals("BR", PaisesService.countryIso2("BRA"));

    }

    @Test
    @DisplayName("Teste de Conversão de ISO2 não padronizado para ISO2 padronizado")
    void deveConverterIso2NaoPadronizadoParaIso2Padronizado() {

        assertEquals("BR", PaisesService.countryIso2("br"));
        assertEquals("DE", PaisesService.countryIso2("de"));

    }

    @Test
    @DisplayName("Teste de Conversão de ISO2 para Valor Inválido")
    void deveTentarConverterIso2ParaValorInvalido() {
        
        assertNull(PaisesService.countryIso2(""));
        assertNull(PaisesService.countryIso2("XYZ"));
        assertNull(PaisesService.countryIso2(null));

    }

}
