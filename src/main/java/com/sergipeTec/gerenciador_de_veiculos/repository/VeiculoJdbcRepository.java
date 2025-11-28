package com.sergipeTec.gerenciador_de_veiculos.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sergipeTec.gerenciador_de_veiculos.model.CarroModel;
import com.sergipeTec.gerenciador_de_veiculos.model.MotoModel;
import com.sergipeTec.gerenciador_de_veiculos.model.VeiculoModel;
import com.sergipeTec.gerenciador_de_veiculos.repository.interfaces.IVeiculoRepository;

/**
 * Repositório que implementa o acesso a dados usando JDBC PURO.
 * Esta abordagem requer o gerenciamento manual de transações e conexões
 * para garantir a atomicidade nas operações de múltiplas tabelas (JOINED SUBCLASS).
 */
@Repository
public class VeiculoJdbcRepository implements IVeiculoRepository {

    // O Spring injeta a fonte de dados configurada em application.properties
    private final DataSource dataSource;

    @Autowired
    public VeiculoJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Query com LEFT JOINs para unir os dados de VEICULOS, CARROS e MOTOS.
    private static final String FIND_ALL_SQL = 
        "SELECT " +
        "v.id, v.modelo, v.fabricante, v.ano, v.preco, v.cor, v.tipo_veiculo," +
        "c.quantidade_portas, c.tipo_combustivel, " +
        "m.cilindrada " +
        "FROM veiculos v " +
        "LEFT JOIN carros c ON v.id = c.veiculo_id " + 
        "LEFT JOIN motos m ON v.id = m.veiculo_id";   

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE v.id = ?";
    
    private static final String INSERT_VEICULO_SQL = 
        "INSERT INTO veiculos (modelo, fabricante, ano, preco, cor, tipo_veiculo) " +
        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id"; // Adicionando RETURNING ID para captura
        
    private static final String INSERT_CARRO_SQL = 
        "INSERT INTO carros (veiculo_id, quantidade_portas, tipo_combustivel) VALUES (?, ?, ?)";
        
    private static final String INSERT_MOTO_SQL = 
        "INSERT INTO motos (veiculo_id, cilindrada) VALUES (?, ?)";

    private static final String DELETE_VEICULO_SQL = "DELETE FROM veiculos WHERE id = ?";

    private static final String UPDATE_VEICULO_SQL = 
        "UPDATE veiculos SET modelo = ?, fabricante = ?, ano = ?, preco = ?, cor = ? WHERE id = ?";
        
    private static final String UPDATE_CARRO_SQL = 
        "UPDATE carros SET quantidade_portas = ?, tipo_combustivel = ? WHERE veiculo_id = ?";
        
    private static final String UPDATE_MOTO_SQL = 
        "UPDATE motos SET cilindrada = ? WHERE veiculo_id = ?";

    @Override
    public VeiculoModel save(VeiculoModel veiculo) throws RuntimeException {
        try (Connection conn = dataSource.getConnection()) {
            
            // 1. DESATIVA O AUTO-COMMIT PARA INICIAR A TRANSAÇÃO MANUALMENTE
            conn.setAutoCommit(false);

            try {
                // --- ETAPA 1: INSERIR NA TABELA BASE (VEICULOS) E CAPTURAR O ID ---
                Long newVeiculoId = null;
                
                try (PreparedStatement stmtVeiculo = conn.prepareStatement(INSERT_VEICULO_SQL)) {
                    String tipo = veiculo.getTipo_veiculo() != null ? veiculo.getTipo_veiculo() : ((veiculo instanceof CarroModel ? "CARRO" : "MOTO"));
                    stmtVeiculo.setString(6, tipo);
                    stmtVeiculo.setString(1, veiculo.getModelo());
                    stmtVeiculo.setString(2, veiculo.getFabricante());
                    stmtVeiculo.setInt(3, veiculo.getAno());
                    stmtVeiculo.setDouble(4, veiculo.getPreco());
                    stmtVeiculo.setString(5, veiculo.getCor());
                    
                    // Executa a query e processa o ID retornado
                    try (ResultSet rs = stmtVeiculo.executeQuery()) {
                        if (rs.next()) {
                            newVeiculoId = rs.getLong(1);
                            veiculo.setId(newVeiculoId);
                            veiculo.setTipo_veiculo(tipo);
                        } else {
                            throw new SQLException("Falha ao recuperar ID gerado após INSERT na tabela veiculos.");
                        }
                    }
                }

                // --- ETAPA 2: INSERIR NA TABELA FILHA (CARROS OU MOTOS) ---

                if (veiculo instanceof CarroModel carro) {
                    try (PreparedStatement stmtCarro = conn.prepareStatement(INSERT_CARRO_SQL)) {
                        stmtCarro.setLong(1, newVeiculoId);
                        stmtCarro.setInt(2, carro.getQuantidadePortas());
                        stmtCarro.setString(3, carro.getTipoCombustivel());
                        stmtCarro.executeUpdate();
                    }
                } else if (veiculo instanceof MotoModel moto) {
                    try (PreparedStatement stmtMoto = conn.prepareStatement(INSERT_MOTO_SQL)) {
                        stmtMoto.setLong(1, newVeiculoId);
                        stmtMoto.setInt(2, moto.getCilindrada());
                        stmtMoto.executeUpdate();
                    }
                }

                // 3. SE AMBAS AS OPERAÇÕES FOREM BEM SUCEDIDAS, O COMMIT É EXECUTADO
                conn.commit();
                
                return veiculo;
                
            } catch (SQLException e) {
                // Se algo falhar, desfaz todas as alterações
                conn.rollback(); 
                throw new RuntimeException("Falha na transação de persistência do veículo.", e);
            } finally {
                conn.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão com o banco de dados.", e);
        }
    }

    @Override
    public VeiculoModel updateVeiculo(VeiculoModel veiculo) {
        // Atualiza campos comuns. Não requer transação, pois é apenas uma tabela.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_VEICULO_SQL)) {
             
            stmt.setString(1, veiculo.getModelo());
            stmt.setString(2, veiculo.getFabricante());
            stmt.setInt(3, veiculo.getAno());
            stmt.setDouble(4, veiculo.getPreco());
            stmt.setString(5, veiculo.getCor());
            stmt.setLong(6, veiculo.getId());
            
            if (stmt.executeUpdate() == 0) {
                 throw new RuntimeException("Veículo com ID " + veiculo.getId() + " não encontrado para atualização.");
            }
            conn.close();
            return veiculo;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar campos do veículo base.", e);
        }
    }

    @Override
    public void updateCarro(CarroModel carro) {
        // Atualiza campos de Carro. Não requer transação.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CARRO_SQL)) {
             
            stmt.setInt(1, carro.getQuantidadePortas());
            stmt.setString(2, carro.getTipoCombustivel());
            stmt.setLong(3, carro.getId());
            
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar campos de Carro.", e);
        } 
    }

    @Override
    public void updateMoto(MotoModel moto) {
        // Atualiza campos de Moto. Não requer transação.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_MOTO_SQL)) {
             
            stmt.setInt(1, moto.getCilindrada());
            stmt.setLong(2, moto.getId());
            
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar campos de Moto.", e);
        }
    }

    // --- Método de Busca Combinada (find by criteria) ---

    @Override
    public List<VeiculoModel> findByCriterio(String modelo, Integer ano, String cor, Double preco) {
        List<VeiculoModel> veiculos = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(FIND_ALL_SQL);
        sqlBuilder.append(" WHERE 1=1 ");

        // Lógica de construção dinâmica da query
        if (modelo != null) {
            sqlBuilder.append(" AND v.modelo ILIKE ?");
            params.add("%" + modelo + "%"); // ILIKE para busca case-insensitive parcial
        }
        if (ano != null) {
            sqlBuilder.append(" AND v.ano = ?");
            params.add(ano);
        }
        if (cor != null) {
            sqlBuilder.append(" AND v.cor ILIKE ?");
            params.add("%" + cor + "%");
        }
        if (preco != null) {
            sqlBuilder.append(" AND v.preco <= ?"); // Busca por preço máximo
            params.add(preco);
        }
        
        String finalSql = sqlBuilder.toString();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(finalSql)) {
            
            // Popula os parâmetros dinamicamente
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    veiculos.add(mapRow(rs));
                }
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veículos por critérios combinados.", e);
        }
        return veiculos;
    }

    /**
     * Mapeia os dados do ResultSet para o objeto Veiculo (Carro ou Moto).
     */
    private VeiculoModel mapRow(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_veiculo");
        VeiculoModel veiculo;

        if ("CARRO".equalsIgnoreCase(tipo)) {
            CarroModel carro = new CarroModel();
            carro.setQuantidadePortas(rs.getInt("quantidade_portas"));
            carro.setTipoCombustivel(rs.getString("tipo_combustivel"));
            veiculo = carro;
        } else if ("MOTO".equalsIgnoreCase(tipo)) {
            MotoModel moto = new MotoModel();
            moto.setCilindrada(rs.getInt("cilindrada"));
            veiculo = moto;
        } else {
            // Se o tipo for desconhecido
            throw new SQLException("Tipo de veículo desconhecido: " + tipo); 
        }

        // Configura campos da classe base
        veiculo.setId(rs.getLong("id"));
        veiculo.setModelo(rs.getString("modelo"));
        veiculo.setFabricante(rs.getString("fabricante"));
        veiculo.setAno(rs.getInt("ano"));
        veiculo.setPreco(rs.getDouble("preco"));
        veiculo.setCor(rs.getString("cor"));
        veiculo.setTipo_veiculo(tipo);
        
        return veiculo;
    }

    @Override
    public List<VeiculoModel> findAll() {
        List<VeiculoModel> veiculos = new ArrayList<>();
        // Usa try-with-resources para garantir que os recursos sejam fechados
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                veiculos.add(mapRow(rs));
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os veículos.", e);
        }
        return veiculos;
    }

    @Override
    public Optional<VeiculoModel> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veículo por ID.", e);
        } 
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_VEICULO_SQL)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar veículo por ID.", e);
        }
    }


}
