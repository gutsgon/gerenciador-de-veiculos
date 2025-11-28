package com.sergipeTec.gerenciador_de_veiculos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO Base para recebimento de dados (Request). 
 * Contém os campos comuns a todos os veículos.
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class VeiculoRequestDTO {
    private Long id;
    private String modelo;
    private String fabricante;
    private Integer ano;
    private Double preco;
    private String cor;
}
