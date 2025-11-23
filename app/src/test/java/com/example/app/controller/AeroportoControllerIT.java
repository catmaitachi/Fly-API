package com.example.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.app.dto.request.AeroportoRequest;
import com.example.app.dto.response.AeroportoResponse;
import com.example.app.repository.AeroportoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AeroportoControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private AeroportoRepository repo;

    private String urlBase() { return "http://localhost:" + port + "/api/v1/aeroportos"; }

    private AeroportoRequest req(String iata) {

        return new AeroportoRequest( "Nome Teste", iata, "Cidade", "BR", -10.0, -50.0, 100 );
    
    }

    @AfterEach
    void limparDados() { repo.deleteAll(); }

    @Test
    @DisplayName("Teste - Criar Aeroporto")
    void deveCriarAeroporto() {

        ResponseEntity<AeroportoResponse> post = rest.postForEntity(urlBase(), req("ZZZ"), AeroportoResponse.class);
        assertEquals(HttpStatus.OK, post.getStatusCode());

        AeroportoResponse body = post.getBody();
        assertNotNull(body);
        assertNotNull(body.getId());

    }

    @Test
    @DisplayName("Teste - Listar Aeroportos")
    void deveListarAeroportos() {

        ResponseEntity<AeroportoResponse[]> resp = rest.getForEntity(urlBase(), AeroportoResponse[].class);
        assertThat(resp.getStatusCode().is2xxSuccessful() || resp.getStatusCode() == HttpStatus.NO_CONTENT).isTrue();
    
    }

    @Test
    @DisplayName("Teste - Buscar por IATA")
    void deveBuscarPorIata() {

        rest.postForEntity(urlBase(), req("ZZZ"), AeroportoResponse.class);
        ResponseEntity<AeroportoResponse> get = rest.getForEntity(urlBase()+"/ZZZ", AeroportoResponse.class);
        assertEquals(HttpStatus.OK, get.getStatusCode());

        AeroportoResponse body = get.getBody();
        assertNotNull(body);
        assertEquals("ZZZ", body.getIata());

    }

    @Test
    @DisplayName("Teste - PUT com IATA já existente deve falhar")
    void deveTentarPutIataExistente() {

        rest.postForEntity(urlBase(), req("ZZZ"), AeroportoResponse.class);
        rest.postForEntity(urlBase(), req("ZZX"), AeroportoResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        AeroportoRequest updateReq = req("ZZX"); // IATA já existente
        HttpEntity<AeroportoRequest> entity = new HttpEntity<>(updateReq, headers);

        ResponseEntity<Void> put = rest.exchange(urlBase()+"/ZZZ", HttpMethod.PUT, entity, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, put.getStatusCode());

    }

    @Test
    @DisplayName("Teste - Deletar aeroporto")
    void deveDeletarAeroporto() {

        rest.postForEntity(urlBase(), req("ZZZ"), AeroportoResponse.class);
        
        rest.delete(urlBase()+"/ZZZ");
        ResponseEntity<AeroportoResponse> afterDelete = rest.getForEntity(urlBase()+"/ZZZ", AeroportoResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDelete.getStatusCode());

    }

}
