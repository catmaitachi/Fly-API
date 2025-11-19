package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.model.Aeroporto; 

import com.example.app.repository.AeroportoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AeroportoService {
    
    @Autowired
    private AeroportoRepository repo;

    public List<Aeroporto> listar() { return (List<Aeroporto>) repo.findAll(); }

    public Boolean existe(String iata) { return repo.existsByIata(iata); }

    public Optional<Aeroporto> buscar(String iata) { return repo.findByIata(iata); }

    @SuppressWarnings("null") // Os DTOs garantem a existência antes de chamar
    public Aeroporto salvar(Aeroporto aeroporto) { return repo.save(aeroporto); }

    public Aeroporto atualizar(String iata, Aeroporto novo) {

        return repo.findByIata(iata).map(atual -> {

            atual.setNome(novo.getNome());
            atual.setIata(novo.getIata());
            atual.setCidade(novo.getCidade());
            atual.setPais(novo.getPais());
            atual.setLatitude(novo.getLatitude());
            atual.setLongitude(novo.getLongitude());
            atual.setAltitude(novo.getAltitude());

            return repo.save(atual);

        }).orElse(null);

    }

    @SuppressWarnings("null") // Os DTOs garantem a existência antes de chamar
    public void deletar(String iata) { repo.findByIata(iata).ifPresent(aeroporto -> repo.delete(aeroporto)); }

}
