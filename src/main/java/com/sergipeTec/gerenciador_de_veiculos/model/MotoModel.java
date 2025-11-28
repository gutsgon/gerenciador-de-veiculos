package com.sergipeTec.gerenciador_de_veiculos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MotoModel extends VeiculoModel{
    private Integer cilindrada;

    public MotoModel(String modelo, String fabricante, Integer ano, Double preco, String cor, Integer cilindrada){
        super(modelo, fabricante, ano, preco, cor);
        this.cilindrada = cilindrada;
    }
}
