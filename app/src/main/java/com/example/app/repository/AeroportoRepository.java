package com.example.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.app.model.Aeroporto;

public interface AeroportoRepository extends CrudRepository<Aeroporto, Long> {

    Optional<Aeroporto> findByIata(String iata);

    Boolean existsByIata(String iata);

}
