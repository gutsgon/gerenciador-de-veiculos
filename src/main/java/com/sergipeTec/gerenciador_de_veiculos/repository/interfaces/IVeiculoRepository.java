package com.sergipeTec.gerenciador_de_veiculos.repository.interfaces;

import java.util.List;
import java.util.Optional;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;

public interface IVeiculoRepository {
    
    /**
     * Salva um veículo no banco de dados (envolvendo múltiplas tabelas).
     * @param veiculo O objeto Veiculo.
     * @return O veículo salvo com o ID.
     */
    VeiculoModel save(VeiculoModel veiculo);

    /**
     * Busca todos os veículos (utilizando JOINs para unir as tabelas).
     * @return Lista de veículos.
     */
    List<VeiculoModel> findAll();

    /**
     * Deleta um veículo pelo ID.
     * @param id O ID do veículo.
     */
    void deleteById(Long id);
    
    /**
     * Busca um veículo pelo ID.
     * @param id O ID do veículo.
     * @return Um Optional contendo o veículo, se encontrado.
     */
    Optional<VeiculoModel> findById(Long id);

    /**
     * Busca veículos por múltiplos critérios combinados (modelo, ano, cor, preco).
     * Todos os parâmetros são opcionais.
     * @param modelo O modelo do veículo (opcional).
     * @param ano O ano de fabricação (opcional).
     * @param cor A cor do veículo (opcional).
     * @param preco O preço máximo do veículo (opcional).
     * @return Lista de veículos que correspondem aos critérios.
     */
    List<VeiculoModel> findByCriterio(String modelo, Integer ano, String cor, Double preco);
    
    // --- Novos Métodos de Atualização (UPDATE) ---

    /**
     * Atualiza os campos comuns (base) de um Veiculo.
     * @param veiculo O objeto Veiculo com os novos dados e o ID para referência.
     * @return O Veiculo atualizado.
     */
    VeiculoModel updateVeiculo(VeiculoModel veiculo);
    
    /**
     * Atualiza os campos específicos de um Carro.
     * @param carro O objeto Carro com os novos dados e o ID para referência.
     */
    void updateCarro(CarroModel carro);
    
    /**
     * Atualiza os campos específicos de uma Moto.
     * @param moto O objeto Moto com os novos dados e o ID para referência.
     */
    void updateMoto(MotoModel moto);
}
