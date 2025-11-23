package com.example.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.app.model.Aeroporto;
import com.example.app.repository.AeroportoRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AeroportoServiceTest {

    @Mock
    private AeroportoRepository repo;

    @InjectMocks
    private AeroportoService service;

    private Aeroporto buildAirport(String iata) {

        Aeroporto gothamPort = new Aeroporto();

        gothamPort.setId(1L);
        gothamPort.setNome("Aeroporto de Gotham");
        gothamPort.setIata(iata);
        gothamPort.setCidade("Gotham");
        gothamPort.setPais("US");
        gothamPort.setLatitude(1.0);
        gothamPort.setLongitude(2.0);
        gothamPort.setAltitude(10);

        return gothamPort;
    
    }

    // Testando a classe repository e service

    @Test
    @DisplayName("Teste - Listar Aeroportos")
    void deveListarAeroportos() {

        when(repo.findAll()).thenReturn(List.of(buildAirport("AAA")));

        List<Aeroporto> hangar = service.listar();

        assertEquals(1, hangar.size());
        assertEquals("AAA", hangar.getFirst().getIata());

    }

    @Test
    @DisplayName("Teste - Verificar Existência")
    void deveVerificarSeAeroportoExiste() {

        when(repo.existsByIata("ZZZ")).thenReturn(true);
        assertTrue(service.existe("ZZZ"));

    }

    @Test
    @DisplayName("Teste - Buscar Aeroporto")
    void deveBuscarAeroporto() {

        Aeroporto skyPort = buildAirport("ABC");

        when(repo.findByIata("ABC")).thenReturn(Optional.of(skyPort));
        assertTrue(service.buscar("ABC").isPresent());

    }

    @Test
    @DisplayName("Teste - Salvar Aeroporto")
    void deveSalvarAeroporto() {

        Aeroporto runway = buildAirport("DEF");

        when(repo.save(runway)).thenReturn(runway);
        
        Aeroporto landed = service.salvar(runway);

        assertEquals("DEF", landed.getIata());
        verify(repo).save(runway);

    }

    @Test
    @DisplayName("Teste - Atualizar Aeroporto")
    void deveAtualizarAeroporto() {

        Aeroporto oldTerminal = buildAirport("OLD");
        when(repo.findByIata("OLD")).thenReturn(Optional.of(oldTerminal));

        Aeroporto incoming = buildAirport("NEW");
        when(repo.save(any(Aeroporto.class))).thenAnswer(inv -> inv.getArgument(0));

        Aeroporto refreshed = service.atualizar("OLD", incoming);
        assertNotNull(refreshed);
        assertEquals("NEW", refreshed.getIata());

        ArgumentCaptor<Aeroporto> captor = ArgumentCaptor.forClass(Aeroporto.class);
        verify(repo).save(captor.capture());
        assertEquals("NEW", captor.getValue().getIata());

    }

    @Test
    @DisplayName("Teste - Atualizar Aeroporto não encontrado")
    void deveAtualizarAeroportoNaoEncontrado() {

        when(repo.findByIata("IDK")).thenReturn(Optional.empty());

        Aeroporto ghost = buildAirport("X");

        assertNull(service.atualizar("IDK", ghost));

    }

    @Test
    @DisplayName("Teste - Deletar Aeroporto")
    void deveDeletarAeroporto() {

        Aeroporto toDelete = buildAirport("DEL");

        when(repo.findByIata("DEL")).thenReturn(Optional.of(toDelete));
        service.deletar("DEL");
        verify(repo).delete(toDelete);
        
    }
}
