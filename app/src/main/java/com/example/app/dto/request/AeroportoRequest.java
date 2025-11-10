package com.example.app.dto.request;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AeroportoRequest {

    @NotBlank @Size(max = 100)
    private String nome;

    @NotBlank @Size(min = 3, max = 3)
    private String iata;

    @NotBlank @Size(max = 100)
    private String cidade;

    @NotBlank @Size(min = 2, max = 2)
    private String pais;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    @NotNull
    private int altitude;

}
