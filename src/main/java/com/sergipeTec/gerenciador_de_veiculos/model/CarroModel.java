package com.sergipeTec.gerenciador_de_veiculos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CarroModel extends VeiculoModel{
    private Integer quantidadePortas;
    private String tipoCombustivel;

    public CarroModel(String modelo, String fabricante, Integer ano, Double preco, String cor, Integer quantidadePortas, String tipoCombustivel){
        super(modelo, fabricante, ano, preco, cor);
        this.quantidadePortas = quantidadePortas;
        this.tipoCombustivel = tipoCombustivel;
    }
    
}
