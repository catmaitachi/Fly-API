package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.mapper.AeroportoMapper;
import com.example.app.dto.request.AeroportoRequest;
import com.example.app.dto.response.AeroportoResponse;
import com.example.app.model.Aeroporto;
import com.example.app.service.AeroportoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/aeroportos")
public class AeroportoController {
    
    @Autowired
    private final AeroportoService aeroportoService;

    @GetMapping
    private ResponseEntity<List<AeroportoResponse>> listarAeroportos() {

        List<AeroportoResponse> aeroportos = aeroportoService.listar()
        .stream()
        .map(AeroportoMapper::fromEntity)
        .toList();

        if ( aeroportos.isEmpty() ) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(aeroportos);

    }

    @GetMapping("/{iata}")
    private ResponseEntity<AeroportoResponse> buscarPorIata( @PathVariable String iata ) {

        return aeroportoService.buscar(iata)
            .map(aeroporto -> ResponseEntity.ok(AeroportoMapper.fromEntity(aeroporto)))
            .orElse(ResponseEntity.notFound().build());
        
    }

    @PostMapping
    private ResponseEntity<AeroportoResponse> salvarAeroporto( @RequestBody @Valid AeroportoRequest r ) {

        Aeroporto aeroporto = AeroportoMapper.toEntity(r);
        aeroporto = aeroportoService.salvar(aeroporto);
        return ResponseEntity.ok(AeroportoMapper.fromEntity(aeroporto));

    }

    @PutMapping("/{iata}")
    private ResponseEntity<AeroportoResponse> atualizarAeroporto( @PathVariable String iata, @RequestBody @Valid AeroportoRequest r ) {

        Boolean exists = aeroportoService.existe(r.getIata());
        if ( exists && !iata.equals(r.getIata()) ) return ResponseEntity.badRequest().build();

        Aeroporto novo = AeroportoMapper.toEntity(r);
        Aeroporto atualizado = aeroportoService.atualizar(iata, novo);

        if ( atualizado != null ) return ResponseEntity.ok( AeroportoMapper.fromEntity(atualizado) );
        else return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{iata}")
    private ResponseEntity<Void> deletarAeroporto( @PathVariable String iata ) {

        aeroportoService.deletar(iata);

        return ResponseEntity.noContent().build();

    }

}
