package com.example.app.dto.request;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank(message = "O nome do aeroporto é obrigatório.")
    @Size(max = 100, message = "O nome do aeroporto não pode exceder 100 caracteres.")
    private String nome;

    @NotBlank(message = "O código IATA é obrigatório.")
    @Size(min = 3, max = 3, message = "O código IATA deve ter exatamente 3 caracteres.")
    private String iata;

    @NotBlank(message = "O nome da cidade é obrigatório.")
    @Size(max = 100, message = "O nome da cidade não pode exceder 100 caracteres.")
    private String cidade;

    @NotBlank(message = "O código do país é obrigatório.")
    @Size(min = 2, max = 2, message = "O código do país deve ter exatamente 2 caracteres.")
    private String pais;

    @NotNull(message = "A latitude é obrigatória.")
    private double latitude;

    @NotNull(message = "A longitude é obrigatória.")
    private double longitude;

    @NotNull(message = "A altitude é obrigatória.")
    @Positive(message = "A altitude deve ser um número positivo.")
    private int altitude;

}
