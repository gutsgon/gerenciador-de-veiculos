package com.sergipeTec.gerenciador_de_veiculos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.service.VeiculoService;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // usa application-test.properties + schema.sql + data.sql
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VeiculoServiceIntegrationTest {

    @Autowired
    private VeiculoService service;

    private static Long idCarro;
    private static Long idMoto;

    @Test
    @Order(1)
    void deveCriarCarroEMoto() {
        // === CARRO ===
        // ATENÇÃO: usar modelo/ano diferentes do data.sql para não violar uk_modelo_ano
        CarroCreateDTO carroDto = new CarroCreateDTO();
        carroDto.setModelo("Corolla");
        carroDto.setFabricante("Toyota");
        carroDto.setAno(2024);
        carroDto.setPreco(150000.0);
        carroDto.setCor("Branco");
        carroDto.setQuantidadePortas(4);
        carroDto.setTipoCombustivel("Gasolina");

        CarroModel carro = service.saveCarro(carroDto);

        assertNotNull(carro.getId());
        assertEquals("CARRO", carro.getTipo_veiculo());
        assertEquals(4, carro.getQuantidadePortas());
        idCarro = carro.getId();

        // === MOTO ===
        MotoCreateDTO motoDto = new MotoCreateDTO();
        motoDto.setModelo("CG 160");
        motoDto.setFabricante("Honda");
        motoDto.setAno(2024);
        motoDto.setPreco(15000.0);
        motoDto.setCor("Vermelho");
        motoDto.setCilindrada(160);

        MotoModel moto = service.saveMoto(motoDto);

        assertNotNull(moto.getId());
        assertEquals("MOTO", moto.getTipo_veiculo());
        assertEquals(160, moto.getCilindrada());
        idMoto = moto.getId();
    }

    @Test
    @Order(2)
    void deveBuscarPorId() {
        Optional<VeiculoModel> optCarro = service.findById(idCarro);
        Optional<VeiculoModel> optMoto = service.findById(idMoto);

        assertTrue(optCarro.isPresent());
        assertTrue(optMoto.isPresent());

        assertTrue(optCarro.get() instanceof CarroModel);
        assertTrue(optMoto.get() instanceof MotoModel);
    }

    @Test
    @Order(3)
    void deveListarTodos() {
        List<VeiculoModel> todos = service.findAll();

        // Já existem pelo menos:
        // - 2 do data.sql (Civic + Fazer)
        // - 2 criados no teste (Corolla + CG 160)
        assertFalse(todos.isEmpty());
        assertTrue(todos.stream().anyMatch(v -> v.getId().equals(idCarro)));
        assertTrue(todos.stream().anyMatch(v -> v.getId().equals(idMoto)));
    }

    @Test
    @Order(4)
    void deveDeletarVeiculos() {
        service.deleteById(idCarro);
        service.deleteById(idMoto);

        assertTrue(service.findById(idCarro).isEmpty());
        assertTrue(service.findById(idMoto).isEmpty());
    }
}
