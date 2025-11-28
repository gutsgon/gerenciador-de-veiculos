package com.sergipeTec.gerenciador_de_veiculos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;
import com.sergipeTec.gerenciador_de_veiculos.repository.interfaces.IVeiculoRepository;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.CarroRequestDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoCreateDTO;
import com.sergipeTec.gerenciador_de_veiculos.rest.dto.MotoRequestDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private IVeiculoRepository repository;

    @InjectMocks
    private VeiculoService service;

    // ---------- SAVE CARRO ----------

    @Test
    void deveSalvarCarro_ComSucesso() {
        CarroCreateDTO dto = new CarroCreateDTO();
        dto.setModelo("Civic");
        dto.setFabricante("Honda");
        dto.setAno(2022);
        dto.setPreco(120000.0);
        dto.setCor("Preto");
        dto.setQuantidadePortas(4);
        dto.setTipoCombustivel("Gasolina");

        CarroModel salvo = new CarroModel(
                dto.getModelo(),
                dto.getFabricante(),
                dto.getAno(),
                dto.getPreco(),
                dto.getCor(),
                dto.getQuantidadePortas(),
                dto.getTipoCombustivel()
        );
        salvo.setId(1L);
        salvo.setTipo_veiculo("CARRO");

        when(repository.save(any(VeiculoModel.class)))
                .thenReturn(salvo);

        CarroModel resultado = service.saveCarro(dto);

        assertNotNull(resultado.getId());
        assertEquals("Civic", resultado.getModelo());
        assertEquals("CARRO", resultado.getTipo_veiculo());
        assertEquals(4, resultado.getQuantidadePortas());
        assertEquals("Gasolina", resultado.getTipoCombustivel());

        verify(repository).save(any(VeiculoModel.class));
    }

    @Test
    void deveLancarErro_QuandoTipoCombustivelInvalido() {
        CarroCreateDTO dto = new CarroCreateDTO();
        dto.setModelo("Civic");
        dto.setFabricante("Honda");
        dto.setAno(2022);
        dto.setPreco(120000.0);
        dto.setCor("Preto");
        dto.setQuantidadePortas(4);
        dto.setTipoCombustivel("alcool"); // não está na lista permitida

        assertThrows(IllegalArgumentException.class, () -> service.saveCarro(dto));
        verify(repository, never()).save(any());
    }

    // ---------- SAVE MOTO ----------

    @Test
    void deveSalvarMoto_ComSucesso() {
        MotoCreateDTO dto = new MotoCreateDTO();
        dto.setModelo("Fazer 250");
        dto.setFabricante("Yamaha");
        dto.setAno(2023);
        dto.setPreco(22000.0);
        dto.setCor("Azul");
        dto.setCilindrada(250);

        MotoModel salvo = new MotoModel(
                dto.getModelo(),
                dto.getFabricante(),
                dto.getAno(),
                dto.getPreco(),
                dto.getCor(),
                dto.getCilindrada()
        );
        salvo.setId(2L);
        salvo.setTipo_veiculo("MOTO");

        when(repository.save(any(VeiculoModel.class)))
                .thenReturn(salvo);

        MotoModel resultado = service.saveMoto(dto);

        assertNotNull(resultado.getId());
        assertEquals("MOTO", resultado.getTipo_veiculo());
        assertEquals(250, resultado.getCilindrada());

        verify(repository).save(any(VeiculoModel.class));
    }

    @Test
    void deveLancarErro_QuandoCilindradaInvalida() {
        MotoCreateDTO dto = new MotoCreateDTO();
        dto.setModelo("Fazer 250");
        dto.setFabricante("Yamaha");
        dto.setAno(2023);
        dto.setPreco(22000.0);
        dto.setCor("Azul");
        dto.setCilindrada(0); // inválida

        assertThrows(IllegalArgumentException.class, () -> service.saveMoto(dto));
        verify(repository, never()).save(any());
    }

    // ---------- UPDATE CARRO ----------

    @Test
    void deveAtualizarCarro_ComSucesso() {
        CarroModel carroExistente = new CarroModel(
                "Civic",
                "Honda",
                2022,
                120000.0,
                "Preto",
                4,
                "Gasolina"
        );
        carroExistente.setId(1L);
        carroExistente.setTipo_veiculo("CARRO");

        when(repository.findById(1L))
                .thenReturn(Optional.of(carroExistente));

        CarroRequestDTO dto = new CarroRequestDTO();
        dto.setId(1L);
        dto.setModelo("Civic Touring");
        dto.setAno(2023);
        dto.setPreco(140000.0);

        CarroModel atualizado = service.updateCarro(dto);

        assertEquals(1L, atualizado.getId());
        assertEquals("Civic Touring", atualizado.getModelo());
        assertEquals(2023, atualizado.getAno());
        assertEquals(140000.0, atualizado.getPreco());

        verify(repository).findById(1L);
        verify(repository).updateVeiculo(any(VeiculoModel.class));
        verify(repository).updateCarro(any(CarroModel.class));
    }

    @Test
    void deveLancarErro_AtualizarCarro_NaoEncontradoOuTipoErrado() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        CarroRequestDTO dto = new CarroRequestDTO();
        dto.setId(99L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateCarro(dto));
        assertTrue(ex.getMessage().contains("Carro não encontrado"));

        verify(repository).findById(99L);
        verify(repository, never()).updateVeiculo(any());
        verify(repository, never()).updateCarro(any());
    }

    // ---------- UPDATE MOTO ----------

    @Test
    void deveAtualizarMoto_ComSucesso() {
        MotoModel motoExistente = new MotoModel(
                "Fazer 250",
                "Yamaha",
                2023,
                22000.0,
                "Azul",
                250
        );
        motoExistente.setId(2L);
        motoExistente.setTipo_veiculo("MOTO");

        when(repository.findById(2L))
                .thenReturn(Optional.of(motoExistente));

        MotoRequestDTO dto = new MotoRequestDTO();
        dto.setId(2L);
        dto.setPreco(23000.0);
        dto.setCilindrada(300);

        MotoModel atualizado = service.updateMoto(dto);

        assertEquals(2L, atualizado.getId());
        assertEquals(23000.0, atualizado.getPreco());
        assertEquals(300, atualizado.getCilindrada());

        verify(repository).findById(2L);
        verify(repository).updateVeiculo(any(VeiculoModel.class));
        verify(repository).updateMoto(any(MotoModel.class));
    }

    // ---------- DELETE ----------

    @Test
    void deveDeletarVeiculo_ComSucesso() {
        VeiculoModel veiculo = new MotoModel();
        veiculo.setId(10L);

        when(repository.findById(10L)).thenReturn(Optional.of(veiculo));
        doNothing().when(repository).deleteById(10L);

        service.deleteById(10L);

        verify(repository).findById(10L);
        verify(repository).deleteById(10L);
    }

    @Test
    void deveLancarErro_QuandoDeletarVeiculoNaoExistente() {
        when(repository.findById(50L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(50L));
        assertTrue(ex.getMessage().contains("Veículo não encontrado"));

        verify(repository).findById(50L);
        verify(repository, never()).deleteById(anyLong());
    }

    // ---------- FINDs simples ----------

    @Test
    void deveRetornarListaDeVeiculos_FindAll() {
        when(repository.findAll()).thenReturn(List.of(new MotoModel(), new CarroModel()));

        List<VeiculoModel> lista = service.findAll();

        assertEquals(2, lista.size());
        verify(repository).findAll();
    }

    @Test
    void deveBuscarPorId() {
        VeiculoModel veiculo = new MotoModel();
        veiculo.setId(3L);
        when(repository.findById(3L)).thenReturn(Optional.of(veiculo));

        Optional<VeiculoModel> opt = service.findById(3L);

        assertTrue(opt.isPresent());
        assertEquals(3L, opt.get().getId());
        verify(repository).findById(3L);
    }
}
