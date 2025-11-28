package com.sergipeTec.gerenciador_de_veiculos.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.EXISTING_PROPERTY, 
    property = "tipo_veiculo", 
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CarroModel.class, name = "CARRO"),
    @JsonSubTypes.Type(value = MotoModel.class, name = "MOTO")
})
public abstract class VeiculoModel {
    protected Long id;
    protected String modelo;
    protected String fabricante;
    protected Integer ano;
    protected Double preco;
    protected String cor;
    protected String tipo_veiculo;

    public VeiculoModel(String modelo, String fabricante, Integer ano, Double preco, String cor){
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.ano = ano;
        this.preco = preco;
        this.cor = cor;
    }
}
