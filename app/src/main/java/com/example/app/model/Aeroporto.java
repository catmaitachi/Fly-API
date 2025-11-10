package com.example.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "aeroporto")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Aeroporto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aeroporto", nullable = false, unique = true)
    private int id;

    @Column(name = "nome_aeroporto", nullable = false)
    private String nome;

    @Column(name = "codigo_iata", nullable = false, unique = true, length = 3)
    private String iata;

    @Column(name = "cidade", nullable = false)
    private String cidade;

    @Column(name = "codigo_pais_iso", nullable = false, length = 2)
    private String pais;

    @Column(name = "latitude", nullable = false)
    private double latitude;
    
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "altitude", nullable = false)
    private int altitude;

}