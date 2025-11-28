package com.sergipeTec.gerenciador_de_veiculos.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO de entrada (Request) para Moto.
 * Herda os campos comuns e adiciona o campo específico de Moto.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MotoCreateDTO extends VeiculoCreateDTO {
    private Integer cilindrada;
}
