# 🚗 Gerenciador de Veículos – Desafio Técnico

Aplicação backend em **Java + Spring Boot** para gerenciar veículos de uma frota,
classificados em **Carros** e **Motos**, com persistência em **PostgreSQL via JDBC**,
testes automatizados com **JUnit** e ambiente de execução via **Docker Compose**.

Este projeto foi desenvolvido como solução para o desafio técnico descrito no arquivo
`DESAFIO - Programador.docx` incluído no repositório.

---

## 🧱 Tecnologias principais

- **Java 17**
- **Spring Boot 4 / Spring Data JDBC**
- **PostgreSQL 15**
- **Maven**
- **JUnit 5 + Mockito**
- **Docker & Docker Compose**
- **Front-end (HTML/CSS/JS Vanilla)**

---

## 📂 Estrutura geral do projeto

```text
gerenciador-de-veiculos/
├── src/
│   ├── main/
│   │   ├── java/com/sergipeTec/gerenciador_de_veiculos/...
│   │   └── resources/
|   |       |── static/
|   |       |   |── app.js
|   |       |   |── index.html
|   |       |   |── style.css
│   │       └── application.properties
│   └── test/
│       ├── java/com/sergipeTec/gerenciador_de_veiculos/...
│       └── resources/
│           ├── application-test.properties
│           ├── schema.sql
│           └── data.sql
├── init.sql
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

- `init.sql` → script usado pelo container **principal** do PostgreSQL (produção/dev).
- `schema.sql` e `data.sql` (em `src/test/resources`) → usados pelo **profile de teste** (`test`) para montar o banco em memória/disco isolado para os testes de integração.
- `front/` → interface web simples em HTML/CSS/JS consumindo a API via `fetch()`.

> Se você não tiver a pasta `front/`, ajuste o trecho acima para refletir onde estão seus arquivos `index.html`, `style.css` e `app.js`.

---

## 🗃️ Banco de Dados

A aplicação trabalha com **três tabelas principais**:

- `veiculos` – tabela base com atributos comuns:
  - `id`, `modelo`, `fabricante`, `ano`, `preco`, `cor`, `tipo_veiculo`
- `carros` – atributos específicos de carros:
  - `veiculo_id`, `quantidade_portas`, `tipo_combustivel`
- `motos` – atributos específicos de motos:
  - `veiculo_id`, `cilindrada`

### Scripts SQL

- `init.sql` (raiz do projeto) – usado na subida do container `db` do Docker para ambiente de execução da API.
- `schema.sql` (testes) – cria as tabelas para o profile `test`.
- `data.sql` (testes) – popula dados iniciais para os testes de integração.

---

## 🐋 Como rodar com Docker

### 1. Pré-requisitos

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Git](https://git-scm.com/install/windows)

### 2. Clone o repositório 
```bash
git clone
https://github.com/gutsgon/gerenciador-de-veiculos.git
```

### 3. Subir tudo (banco + testes + API)

Na raiz do projeto:

```bash
docker-compose up --build
```

O que esse comando faz:

- Sobe o container **`db`** com PostgreSQL 15
  - Banco: `gerenciador_veiculos`
  - Usuário: `user`
  - Senha: `password`
  - Executa automaticamente o script `init.sql`
- Executa o serviço **`tests`**
  - Usa a imagem `maven:3.8.5-openjdk-17`
  - Roda `mvn clean test` com o profile `test`
  - Conecta no mesmo banco `db` (mas usando `schema.sql` + `data.sql` do profile de teste)
- Após o banco estar saudável, sobe o serviço **`api`**
  - Builda a imagem usando o `Dockerfile`
  - Executa o jar `gerenciador-de-veiculos-1.0.jar`
  - Expõe a API na porta `8080`

> ⚠ **Importante**: o serviço `tests` é pensado para rodar os testes automaticamente na subida.  
> Se você quiser subir somente `db` + `api`, pode fazer:
>
> ```bash
> docker-compose up --build db api
> ```

### 4. Ver logs (incluindo testes)

```bash
# acompanhar tudo em tempo real
docker-compose logs -f

# logs apenas dos testes
docker-compose logs -f tests

# logs apenas da API
docker-compose logs -f api
```

### 5. Parar e remover containers

```bash
docker-compose down
```

Se quiser limpar volumes também:

```bash
docker-compose down -v
```

---

## 🖥️ Front-end (UI) — Como acessar

Este repositório inclui uma interface web simples (**HTML/CSS/JS Vanilla**) para consumo da API.

### Como abrir
1. Suba a API (via Docker ou localmente).
2. Abra o arquivo `front/index.html` no navegador.

### Configuração da URL da API
No arquivo `front/app.js`, a URL base está definida em:

```js
const API_BASE = "http://localhost:8080";
```

Se você rodar a API em outra porta/host, atualize esse valor.

### Exceções e observações do Front
- A UI é **estática** (não há servidor front). Por isso, o acesso é feito abrindo o `index.html` diretamente.
- Como a UI consome a API por `fetch()` em `http://localhost:8080`, pode haver **restrição de CORS** dependendo do seu navegador/configuração.
  - Se isso acontecer, execute a UI através de um servidor local simples (ex.: `Live Server` do VSCode) ou habilite CORS na API.
- Ajuste de layout: foi aplicada uma correção de **grid/spacing** no formulário de filtros para evitar campos “espalhados” em telas largas:
  - o formulário de filtros usa a classe extra `form-grid--filters`
  - os botões (`form-actions`) ocupam a linha toda (`grid-column: 1 / -1`)
  - foi adicionada a classe global `.hidden { display:none !important; }` para suportar corretamente alternâncias de modal/detalhes usadas no JavaScript.

---

## ▶️ Rodando localmente (sem Docker)

1. **Subir um PostgreSQL local** (ou usar um existente) com as credenciais desejadas.
2. Rodar o script `init.sql` no banco escolhido (para criar as tabelas).
3. Ajustar o `application.properties` com a URL, usuário e senha do seu banco, por exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gerenciador_veiculos
spring.datasource.username=user
spring.datasource.password=password

spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=none
```

4. Rodar a aplicação com Maven:

```bash
mvn spring-boot:run
```

A API ficará disponível localmente e no Docker em:

```text
http://localhost:8080
```

```text
http://localhost:8080/swagger-ui/index.html#/
```

---

## 🧪 Executando os testes

### Via Maven (local)

```bash
mvn clean test
```

- Usa o profile `test`
- Sobe o contexto Spring Boot de teste
- Usa `schema.sql` + `data.sql` em `src/test/resources`

### Via Docker (serviço `tests`)

```bash
docker-compose run --rm tests
```

- Sobe o container de testes
- Executa `mvn clean test` dentro do container
- Remove o container ao final (`--rm`)

---

## 🌐 Endpoints principais da API

> Os endpoints podem variar conforme o controlador, mas a ideia geral é esta:

### 🔹 Veículos (genérico)

- `GET /veiculos` → Lista todos os veículos.
- `GET /veiculos/{id}` → Busca veículo por ID.
- `GET /veiculos/busca` → Busca com filtros.
- `DELETE /veiculos/{id}` → Remove veículo por ID.

### 🔹 Carros

- `POST /carros` → Cadastra um carro.
- `PATCH /carros` ou endpoint equivalente → Atualiza um carro existente.
- Filtros (exemplo): `/carros?modelo=Civic&cor=Preto&ano=2022`

### 🔹 Motos

- `POST /motos` → Cadastra uma moto.
- `PATCH /motos` ou endpoint equivalente → Atualiza uma moto existente.
- Filtros (exemplo): `/motos?modelo=Fazer&ano=2023`

> ✅ Os filtros e mapeamentos exatos estão implementados nos controllers e repositórios com **queries nativas** via JDBC.

---

## 🧭 Regras de negócio implementadas

1. **Atributos obrigatórios de todo veículo**
   - `modelo`, `fabricante`, `ano`, `preco`, `cor`.
2. **Carros**
   - `quantidadePortas` obrigatório.
   - `tipoCombustivel` obrigatório (valores aceitos: `gasolina`, `etanol`, `diesel`, `flex` – validação feita no service).
3. **Motos**
   - `cilindrada` obrigatória e deve ser maior que zero.
4. **Integridade referencial**
   - As tabelas filhas (`carros`, `motos`) possuem `veiculo_id` referenciando `veiculos(id)` com **ON DELETE CASCADE**.
5. **Consulta e filtros**
   - Endpoints para buscar por tipo, modelo, cor e ano.
6. **CRUD completo**
   - Criar, listar, detalhar, atualizar e excluir veículos.

---

## ✏️ Commits e versionamento (Git)

Este repositório segue as orientações do desafio:

- Commits com mensagens claras e objetivas, por exemplo:
  - `feat: implementar cadastro de carros`
  - `feat: adicionar filtro por modelo e cor`
  - `test: criar testes de integração para VeiculoService`
  - `chore: configurar Dockerfile e docker-compose`
  - `docs: adicionar instruções de execução no README`
- Histórico preservado, sem reescrever a história após publicado.

Sugestão de fluxo local:

```bash
git add .
git commit -m "feat: implementar CRUD básico de veículos"

git remote add origin https://github.com/SEU_USUARIO/gerenciador-de-veiculos.git
git push -u origin main
```
