package com.sergipeTec.gerenciador_de_veiculos.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO de entrada (Request) para Carro. 
 * Herda os campos comuns e adiciona os específicos de Carro.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CarroCreateDTO extends VeiculoCreateDTO {
    private Integer quantidadePortas;
    private String tipoCombustivel; 
}