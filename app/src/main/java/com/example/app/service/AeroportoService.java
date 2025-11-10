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

    public Aeroporto salvar(Aeroporto aeroporto) { return repo.save(aeroporto); }

    public Aeroporto atualizar(String iata, Aeroporto novo) {

        Optional<Aeroporto> aOpt = repo.findByIata(iata);

        if ( aOpt.isPresent() ) {

            Aeroporto atual = aOpt.get();

            atual.setNome(novo.getNome());
            atual.setIata(novo.getIata());
            atual.setCidade(novo.getCidade());
            atual.setPais(novo.getPais());
            atual.setLatitude(novo.getLatitude());
            atual.setLongitude(novo.getLongitude());
            atual.setAltitude(novo.getAltitude());

            return repo.save(atual);

        } else return null;

    }

    public void deletar(String iata) {

        Optional<Aeroporto> aOpt = repo.findByIata(iata);

        aOpt.ifPresent(aeroporto -> repo.delete(aeroporto));

    }

}
