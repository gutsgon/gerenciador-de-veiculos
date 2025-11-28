package com.sergipeTec.gerenciador_de_veiculos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuração do Springdoc OpenAPI (Swagger).
 * Define metadados como Título, Descrição e Versão da API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Gestão de Veículos")
                .version("1.0.0")
                .description("API REST para cadastro, busca e atualização de veículos"));
    }
}