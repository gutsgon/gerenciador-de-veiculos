package com.sergipeTec.gerenciador_de_veiculos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;
import com.sergipeTec.gerenciador_de_veiculos.repository.interfaces.IVeiculoRepository;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.exception.VeiculoNaoEncontradoException;
import com.sergipeTec.gerenciador_de_veiculos.service.interfaces.IVeiculoService;

/**
 * Implementação da camada de Serviço, contendo a lógica de negócio.
 */
@Service
public class VeiculoService implements IVeiculoService {

    private final IVeiculoRepository repository;

    @Autowired
    public VeiculoService(IVeiculoRepository repository) {
        this.repository = repository;
    }

    private void validarVeiculo(VeiculoModel veiculo) throws IllegalArgumentException {
        // --- REGRAS DE NEGÓCIO ---
        if (veiculo.getAno() > java.time.Year.now().getValue() + 1) {
            throw new IllegalArgumentException("O ano de fabricação é inválido.");
        }
        if (veiculo.getPreco() <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        if(veiculo.getCor() == null){
            throw new IllegalArgumentException("O veículo deve ter uma cor");
        }
        if(veiculo.getModelo() == null){
            throw new IllegalArgumentException("O veículo deve ter um modelo");
        }
        if(veiculo.getAno() < 1886){
            throw new IllegalArgumentException("O ano de fabricação é inválido.");
        }
        if(veiculo.getAno() == null){
            throw new IllegalArgumentException("O ano de fabricação é obrigatório.");
        }
        if(veiculo.getFabricante() == null){
            throw new IllegalArgumentException("O veículo deve ter um fabricante");
        }
        if(veiculo.getPreco() == null){
            throw new IllegalArgumentException("O preço é obrigatório");
        }
    }

    private void validarAtualizacaoVeiculo(VeiculoModel veiculo) {
        // --- REGRAS DE NEGÓCIO ---
        if(veiculo.getAno() != null){
            if (veiculo.getAno() > java.time.Year.now().getValue() + 1 || veiculo.getAno() < 1886) {
                throw new IllegalArgumentException("O ano de fabricação é inválido.");
            }
        }

        if(veiculo.getPreco() != null){
            if (veiculo.getPreco() <= 0) {
                throw new IllegalArgumentException("O preço deve ser maior que zero.");
            }
        }

        if(veiculo.getAno() != null){
            if(veiculo.getAno() < 1886){
                throw new IllegalArgumentException("O ano de fabricação é inválido.");
            }
        }
    }

    @Override
    public CarroModel saveCarro(CarroCreateDTO carroDTO){
        CarroModel carroModel = new CarroModel(carroDTO.getModelo(), carroDTO.getFabricante(), carroDTO.getAno(), carroDTO.getPreco(), carroDTO.getCor(), carroDTO.getQuantidadePortas(), carroDTO.getTipoCombustivel());
        validarVeiculo(carroModel);
        List<String> tipos = List.of("gasolina","etanol","diesel","flex");
        if(!tipos.contains(carroDTO.getTipoCombustivel().toLowerCase())) throw new IllegalArgumentException("Tipo de combustível inválido");
        if(carroDTO.getQuantidadePortas() <= 0 || carroDTO.getTipoCombustivel().isEmpty()) throw new IllegalArgumentException("Quantidade de portas ou tipo de combustível inválido");
        return (CarroModel) repository.save(carroModel);
    }

    @Override
    public MotoModel saveMoto(MotoCreateDTO motoDTO){
        MotoModel motoModel = new MotoModel(motoDTO.getModelo(), motoDTO.getFabricante(), motoDTO.getAno(), motoDTO.getPreco(), motoDTO.getCor(), motoDTO.getCilindrada());
        validarVeiculo(motoModel);
        if(motoDTO.getCilindrada() <= 0) throw new IllegalArgumentException("Cilindrada inválida");
        return (MotoModel) repository.save(motoModel);
    }

    @Override
    public CarroModel updateCarro(CarroRequestDTO carroDTO){
        Optional<VeiculoModel> veiculo = repository.findById(carroDTO.getId());
        if(veiculo.isEmpty() || !(veiculo.get() instanceof CarroModel)){
            throw new VeiculoNaoEncontradoException("Carro não encontrado com o ID: " + carroDTO.getId());
        }
        CarroModel carroModel = (CarroModel) veiculo.get();
        if (carroDTO.getModelo() != null) {
            carroModel.setModelo(carroDTO.getModelo());
        }
        if(carroDTO.getFabricante() != null){
            carroModel.setFabricante(carroDTO.getFabricante());
        }
        if (carroDTO.getAno() != null) {
            carroModel.setAno(carroDTO.getAno());
        }
        if (carroDTO.getPreco() != null) {
            carroModel.setPreco(carroDTO.getPreco());
        }
        if (carroDTO.getCor() != null) {
            carroModel.setCor(carroDTO.getCor());
        }
        if (carroDTO.getQuantidadePortas() != null){
            carroModel.setQuantidadePortas(carroDTO.getQuantidadePortas());
        }
        if (carroDTO.getTipoCombustivel() != null){
            carroModel.setTipoCombustivel(carroDTO.getTipoCombustivel());
        }
        validarAtualizacaoVeiculo(carroModel);

        repository.updateVeiculo(carroModel);
        repository.updateCarro(carroModel);

        return carroModel;
    }

    @Override
    public MotoModel updateMoto(MotoRequestDTO motoDTO){
        Optional<VeiculoModel> veiculo = repository.findById(motoDTO.getId());
        if(veiculo.isEmpty() || !(veiculo.get() instanceof MotoModel)){
            throw new VeiculoNaoEncontradoException("Moto não encontrada com o ID: " + motoDTO.getId());
        }
        MotoModel motoModel = (MotoModel) veiculo.get();
        if (motoDTO.getModelo() != null) {
            motoModel.setModelo(motoDTO.getModelo());
        }
        if(motoDTO.getFabricante() != null){
            motoModel.setFabricante(motoDTO.getFabricante());
        }
        if (motoDTO.getAno() != null) {
            motoModel.setAno(motoDTO.getAno());
        }
        if (motoDTO.getPreco() != null) {
            motoModel.setPreco(motoDTO.getPreco());
        }
        if (motoDTO.getCor() != null) {
            motoModel.setCor(motoDTO.getCor());
        }
        if (motoDTO.getCilindrada() != null){
            motoModel.setCilindrada(motoDTO.getCilindrada());
        }
        validarAtualizacaoVeiculo(motoModel);

        repository.updateVeiculo(motoModel);
        repository.updateMoto(motoModel);

        return motoModel;
    }

    @Override
    public List<VeiculoModel> findByCriterio(String modelo, Integer ano, String cor, Double preco) {
        return repository.findByCriterio(modelo, ano, cor, preco);
    }

    @Override
    public List<VeiculoModel> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<VeiculoModel> findById(Long id){
        return repository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new RuntimeException("Veículo não encontrado com o ID: " + id); 
        }
        repository.deleteById(id);
    }
}