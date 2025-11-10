package com.example.app.dto.response;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AeroportoResponse {
    
    private Long id;

    private String nome;

    private String iata;

    private String cidade;

    private String pais;

    private double latitude;

    private double longitude;

    private int altitude;

}
