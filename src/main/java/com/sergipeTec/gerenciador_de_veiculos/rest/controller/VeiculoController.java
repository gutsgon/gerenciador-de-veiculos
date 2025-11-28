package com.sergipeTec.gerenciador_de_veiculos.rest.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.service.interfaces.IVeiculoService;

/**
 * Controller que expõe a API REST, agora com endpoints e DTOs específicos
 * para garantir melhor documentação (Swagger) e separação de preocupações.
 */
@RestController
@RequestMapping("/veiculos")
@CrossOrigin(origins = "*", 
    allowedHeaders= "*", 
    methods = {RequestMethod.GET, 
    RequestMethod.DELETE, 
    RequestMethod.POST, 
    RequestMethod.PATCH}) 
public class VeiculoController {

    private final IVeiculoService veiculoService;

    @Autowired
    public VeiculoController(IVeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    // --- 1. Listagem e Busca ---
    
    // GET /veiculos - Lista todos os veículos (mantendo o retorno polimórfico)
    @GetMapping
    public List<VeiculoModel> listarTodos() {
        return veiculoService.findAll();
    }
    
    // GET /veiculos/busca - Busca combinada por modelo, ano, cor e preço (opcionais)
    @GetMapping("/busca")
    public List<VeiculoModel> buscarPorCriterios(
        @RequestParam(required = false) String modelo,
        @RequestParam(required = false) Integer ano,
        @RequestParam(required = false) String cor,
        @RequestParam(required = false) Double precoMaximo) 
    {
        return veiculoService.findByCriterio(modelo, ano, cor, precoMaximo);
    }

    // GET /veiculos/{id} - Lista todos os veículos (mantendo o retorno polimórfico)
    @GetMapping("/{id}")
    public Optional<VeiculoModel> listarPorId(@PathVariable Long id) {
        return veiculoService.findById(id);
    }


    // --- 2. Cadastro (POST) ---

    // POST /veiculos/carros - Endpoint dedicado para Carro
    @PostMapping("/carros")
    public ResponseEntity<CarroModel> cadastrarCarro(@RequestBody CarroCreateDTO dto) {
            CarroModel savedCarro = veiculoService.saveCarro(dto);
            return ResponseEntity.status(201).body(savedCarro);
    }

    // POST /veiculos/motos - Endpoint dedicado para Moto
    @PostMapping("/motos")
    public ResponseEntity<MotoModel> cadastrarMoto(@RequestBody MotoCreateDTO dto) {
        MotoModel savedMoto = veiculoService.saveMoto(dto); 
        return ResponseEntity.status(201).body(savedMoto);
    }

    // --- 3. Atualização (PATCH/PUT) ---
    
    // PATCH /veiculos/carros - Atualiza Carro
    @PatchMapping("/carros")
    public ResponseEntity<?> atualizarCarro(@RequestBody CarroRequestDTO dto) {
        VeiculoModel updatedCarro = veiculoService.updateCarro(dto);
        return new ResponseEntity<>(updatedCarro, HttpStatus.OK);
    }

    // PATCH /veiculos/motos/{id} - Atualiza Moto
    @PatchMapping("/motos")
    public ResponseEntity<?> atualizarMoto(@RequestBody MotoRequestDTO dto) {
        VeiculoModel updatedMoto = veiculoService.updateMoto(dto);
        return new ResponseEntity<>(updatedMoto, HttpStatus.OK);
    }

    // --- 4. Deleção (DELETE) ---

    // DELETE /veiculos/{id} - Deleta um veículo por ID (Geral)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veiculoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
}