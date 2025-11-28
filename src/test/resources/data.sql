-- Veículos iniciais
INSERT INTO veiculos (id, modelo, fabricante, ano, preco, cor, tipo_veiculo) VALUES
    (1, 'Civic', 'Honda', 2022, 120000.00, 'Preto', 'CARRO'),
    (2, 'Fazer 250', 'Yamaha', 2023, 22000.00, 'Azul', 'MOTO');

-- Carro relacionado ao veículo 1
INSERT INTO carros (veiculo_id, quantidade_portas, tipo_combustivel) VALUES
    (1, 4, 'Gasolina');

-- Moto relacionada ao veículo 2
INSERT INTO motos (veiculo_id, cilindrada) VALUES
    (2, 250);

-- >>> PARTE IMPORTANTE <<<
-- Ajusta a sequence para não gerar novamente o id 1/2,
-- fazendo o próximo nextval() devolver 3 em diante.
SELECT setval(
    pg_get_serial_sequence('veiculos', 'id'),
    (SELECT MAX(id) FROM veiculos)
);
