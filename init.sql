-- 1. TABELA BASE: Veiculos (campos comuns a Carro e Moto)
CREATE TABLE IF NOT EXISTS veiculos (
    id BIGSERIAL PRIMARY KEY, 
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    ano INTEGER NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    cor VARCHAR(50) NOT NULL,
    tipo_veiculo VARCHAR(50) NOT NULL,
    
    -- Restrições de integridade dos veículos
    CONSTRAINT uk_modelo_ano UNIQUE (modelo, ano)
);

-- 2. TABELA FILHA: Carros (campos específicos de Carro)
CREATE TABLE IF NOT EXISTS carros (
    veiculo_id BIGINT PRIMARY KEY,
    quantidade_portas INTEGER NOT NULL,
    tipo_combustivel VARCHAR(50) NOT NULL,
    
    -- Integridade Referencial: veiculo_id é PK e FK para a tabela base.
    -- ON DELETE CASCADE garante que, ao deletar o registro em 'veiculos', 
    -- o registro em 'carros' também seja deletado automaticamente.
    CONSTRAINT fk_carro_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id)
        ON DELETE CASCADE
);

-- 3. TABELA FILHA: Motos (campos específicos de Moto)
CREATE TABLE IF NOT EXISTS motos (
    veiculo_id BIGINT PRIMARY KEY,
    cilindrada INTEGER NOT NULL,
    
    -- Integridade Referencial: veiculo_id é PK e FK para a tabela base.
    CONSTRAINT fk_moto_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id)
        ON DELETE CASCADE
);

-- Inserção de dados iniciais (Para testes)
INSERT INTO veiculos (modelo, fabricante, ano, preco, cor, tipo_veiculo) VALUES
    ('Civic', 'Honda', 2022, 120000.00, 'Preto', 'CARRO'),
    ('Fazer 250', 'Yamaha', 2023, 22000.00, 'Azul','MOTO');

INSERT INTO carros (veiculo_id, quantidade_portas, tipo_combustivel) VALUES
    ((SELECT id FROM veiculos WHERE modelo = 'Civic'), 4, 'Gasolina');

INSERT INTO motos (veiculo_id, cilindrada) VALUES
    ((SELECT id FROM veiculos WHERE modelo = 'Fazer 250'), 250);