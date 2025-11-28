DROP TABLE IF EXISTS motos CASCADE;
DROP TABLE IF EXISTS carros CASCADE;
DROP TABLE IF EXISTS veiculos CASCADE;

-- Tabela base de veículos
CREATE TABLE veiculos (
    id BIGSERIAL PRIMARY KEY,
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    ano INTEGER NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    cor VARCHAR(50) NOT NULL,
    tipo_veiculo VARCHAR(50) NOT NULL,
    CONSTRAINT uk_modelo_ano UNIQUE (modelo, ano)
);

-- Tabela filha: carros
CREATE TABLE carros (
    veiculo_id BIGINT PRIMARY KEY,
    quantidade_portas INTEGER NOT NULL,
    tipo_combustivel VARCHAR(50) NOT NULL,
    CONSTRAINT fk_carro_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id)
        ON DELETE CASCADE
);

-- Tabela filha: motos
CREATE TABLE motos (
    veiculo_id BIGINT PRIMARY KEY,
    cilindrada INTEGER NOT NULL,
    CONSTRAINT fk_moto_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id)
        ON DELETE CASCADE
);
