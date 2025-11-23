```
  _________________  __     _______________________
  ___  ____/__  /_ \/ /     ___    |__  __ \___  _/
  __  /_   __  / __  /________  /| |_  /_/ /__  /  
  _  __/   _  /___  /_/_____/  ___ |  ____/__/ /   
  /_/      /_____/_/        /_/  |_/_/     /___/   by Catmaitachi

```

<p align="center">
  <a href="https://www.docker.com/"><img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white" alt="Docker"/></a>
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white" alt="Java 25"/></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot"/></a>
  <a href="https://www.mysql.com/"><img src="https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white" alt="MySQL"/></a>
</p>

## ✈️ Sobre o projeto

Fly-API é uma aplicação RESTful desenvolvida em Java com Spring Boot, que gerencia informações de aeroportos. Ao iniciar, a aplicação importa dados de aeroportos a partir de um arquivo CSV disponibilizado pela OpenFlights, normaliza os nomes dos países para seus códigos ISO2, converte altitudes de pés para metros e armazena os aeroportos únicos em um banco de dados MySQL. A API oferece operações CRUD (Create, Read, Update, Delete) para manipular os dados dos aeroportos.

Esse projeto foi desenvolvido como método de aprendizado e avaliação da diciplina de Programação Modular do curso de Engenharia de Software da PUC Minas.

## 🚀 Execução com Docker
### Pré-requisitos
- Docker & Docker Compose
- Portas livres: `8080` (API) e `3306` (MySQL)

### Passos
```bash
git clone https://github.com/catmaitachi/Fly-API.git
cd Fly-API/app
docker compose up --build -d
```

### Verificar containers
```bash
docker compose ps
```

### Logs
```bash
docker compose logs -f app
docker compose logs -f mysql
```

### Acesso
`http://localhost:8080`

## 🗄️ Banco de Dados
Configuração padrão (Docker Compose):
- Banco: `flyapi`
- Usuário: `root`
- Senha: `777`
- Porta: `3306`

Conexão:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p
# Senha: 777
```

## 🔧 Comandos Úteis
```bash
# Parar containers
docker compose down

# Parar e remover volumes (apaga dados)
docker compose down -v

# Reconstruir após mudanças no código
docker compose up --build -d

# Reiniciar somente a aplicação
docker compose restart app

# Entrar no container da aplicação
docker compose exec app bash

# Acessar MySQL dentro do container
docker compose exec mysql mysql -u root -p
```

## 🐳 Dockerfile (Resumo)
Multi-stage:
1. `test`: Executa `./mvnw test` (falha interrompe pipeline)
2. `build`: Baixa dependências + empacota
3. `runtime`: Copia JAR final e expõe porta 8080

## 📝 Desenvolvimento Local (sem Docker)
Pré-requisitos: Java 25 + Maven + MySQL.
```bash
cd app
./mvnw spring-boot:run
```
Variáveis e credenciais podem ser ajustadas em `src/main/resources/application.properties`.

## ⚙️ Configuração / Ambiente
- Importação CSV: arquivo `src/main/resources/data/airports.csv`
- Países: conversão robusta (nomes alternativos + ISO2/ISO3)
- Validações: anotadas no DTO `AeroportoRequest`

## 🔌 Endpoints REST
Base URL: `http://localhost:8080/api/v1/aeroportos`

| Método | Rota | Descrição | Respostas |
|--------|------|-----------|-----------|
| GET | `/` | Lista aeroportos | 200 (JSON) ou 204 (sem conteúdo) |
| GET | `/{iata}` | Busca aeroporto pelo código IATA | 200 (JSON) ou 404 (não encontrado) |
| POST | `/` | Cria aeroporto | 200 (criado) / 400 (validação) |
| PUT | `/{iata}` | Atualiza aeroporto | 200 (OK), 400 (IATA destino já existe), 404 (origem não existe) |
| DELETE | `/{iata}` | Remove aeroporto | 204 (Sem conteúdo) |

Exemplo de criação:
```bash
curl -X POST http://localhost:8080/api/v1/aeroportos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Aeroporto Exemplo",
    "iata": "ABC",
    "cidade": "Cidade",
    "pais": "BR",
    "latitude": -10.0,
    "longitude": -50.0,
    "altitude": 100
  }'
```

## 📦 Modelo de Dados
```json
{
  "id": 1,
  "nome": "Aeroporto Exemplo",
  "iata": "ABC",
  "cidade": "Cidade",
  "pais": "BR",
  "latitude": -10.0,
  "longitude": -50.0,
  "altitude": 100 // em metros
}
```

## 🧪 Testes
Estratégia dupla:
- Unitários: `*Test.java` (Surefire) – serviços e utilidades com Mockito/JUnit.
- Integração: `*IT.java` (Failsafe) – sobe contexto web e testa endpoints reais.

Comandos principais:
```bash
# Dentro do diretório app
cd app

# Somente testes unitários
./mvnw test

# Unitários + integração 
./mvnw verify
```

### Testes via Docker
O `docker compose up` usa a imagem de estágio `test` para garantir que os testes passem antes de subir `app`.


## 🔍 Troubleshooting
```bash
# Ver processo usando porta 8080
lsof -i :8080

# Alterar porta em compose.yaml
# ports:
#   - "8081:8080"

# Ver saúde do MySQL
docker compose ps
docker compose logs mysql

# Rebuild completo
docker compose down -v
docker rmi fly-api:local
docker compose up --build -d
```

## 📁 Estrutura 
```text
app/                       # 📂 Projeto
├── Dockerfile             # 🐳 Build multi-stage
├── compose.yaml           # 🧩 Orquestração Docker
├── pom.xml                # 📦 Dependências Maven
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/example/app/
│   │   │       ├── controller/        # 🌐 Endpoints REST
│   │   │       ├── service/           # ⚙️ Regras de negócio / importação
│   │   │       ├── repository/        # 🗄️ Persistência (Spring Data JPA)
│   │   │       ├── dto/               # 🔄 Estrutura de entrada e saída + mappers
│   │   │       └── model/             # 🧱 Entidades (Aeroporto)
│   │   └── resources/
│   │       ├── application.properties # 🔧 Configurações
│   │       └── data/
│   │           └── airports.csv       # ✈️ Fonte de dados (CSV)
│   └── test/
│       └── java/                      # 🧪 Testes unitários + integração
└── target/                            # 🚀 Artefatos gerados (build)
```
