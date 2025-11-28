package com.sergipeTec.gerenciador_de_veiculos.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoCreateDTO;
/**
 * Interface que define a camada de Serviço (Regras de Negócio).
 * Isolamento entre Controller e Repositório.
 */
public interface IVeiculoService {
    
    /**
     * Salva um Carro (Mapeando do DTO).
     */
    CarroModel saveCarro(CarroCreateDTO dto);
    
    /**
     * Salva uma Moto (Mapeando do DTO).
     */
    MotoModel saveMoto(MotoCreateDTO dto);
    
    /**
     * Atualiza um veículo com os dados de um DTO. 
     * @param id ID do veículo a ser atualizado.
     * @param dto DTO com os novos dados.
     * @return O objeto Veiculo atualizado.
     */
    VeiculoModel updateCarro(CarroRequestDTO dto);
    
    /**
     * Atualiza uma moto com os dados de um DTO. 
     * @param id ID da moto a ser atualizada.
     * @param dto DTO com os novos dados.
     * @return O objeto Veiculo atualizado.
     */
    VeiculoModel updateMoto(MotoRequestDTO dto);

    /**
     * Lista todos os veículos.
     * @return Lista de todos os veículos.
     */
    List<VeiculoModel> findAll();

    /**
     * Busca um veículo pelo ID.
     * @param id O ID do veículo.
     */
    Optional<VeiculoModel> findById(Long id);

    /**
     * Busca veículos por múltiplos critérios combinados (modelo, ano, cor, preco).
     * @param modelo O modelo do veículo (opcional).
     * @param ano O ano de fabricação (opcional).
     * @param cor A cor do veículo (opcional).
     * @param preco O preço máximo do veículo (opcional).
     * @return Lista de veículos que correspondem aos critérios.
     */
    List<VeiculoModel> findByCriterio(String modelo, Integer ano, String cor, Double preco);

    /**
     * Deleta um veículo pelo ID.
     * @param id O ID do veículo.
     */
    void deleteById(Long id);
}