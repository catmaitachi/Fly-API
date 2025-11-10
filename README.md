# Fly-API
Trabalho Prático API Rest: Gerenciamento de Aeroportos

## 🚀 Como Rodar o Projeto com Docker

### Pré-requisitos
- [Docker](https://docs.docker.com/get-docker/) instalado
- [Docker Compose](https://docs.docker.com/compose/install/) instalado
- Porta **8080** livre no host (para a API)
- Porta **3306** livre no host (para o MySQL)

### 1️⃣ Clonar o Repositório
```bash
git clone https://github.com/catmaitachi/Fly-API.git
cd Fly-API/app
```

### 2️⃣ Subir os Containers
```bash
docker compose up --build -d
```

**O que acontece:**
- 🔨 Compila a aplicação Spring Boot (Java 25)
- 🐳 Cria a imagem Docker `fly-api:local`
- 🗄️ Sobe o MySQL 8.4 com banco `flyapi`
- 🚀 Inicia a API na porta 8080

### 3️⃣ Verificar Status
```bash
docker compose ps
```

Você deve ver:
```
NAME           IMAGE            STATUS          PORTS
app-app-1      fly-api:local    Up X seconds    0.0.0.0:8080->8080/tcp
app-mysql-1    mysql:8.4        Up X seconds    0.0.0.0:3306->3306/tcp
```

### 4️⃣ Ver Logs
```bash
# Logs da aplicação
docker compose logs -f app

# Logs do MySQL
docker compose logs -f mysql
```

### 5️⃣ Acessar a API
A aplicação estará disponível em:
```
http://localhost:8080
```

---

## 📊 Configuração do Banco de Dados

O Docker Compose cria automaticamente:
- **Banco**: `flyapi`
- **Usuário**: `root`
- **Senha**: `777`
- **Porta**: `3306`

### Conectar via Cliente MySQL
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p
# Senha: 777
```

Ou use ferramentas visuais como **DBeaver**, **MySQL Workbench**, etc.

---

## 🛠️ Comandos Úteis

### Parar os containers
```bash
docker compose down
```

### Parar e remover volumes (apaga dados do banco)
```bash
docker compose down -v
```

### Reconstruir após alterações no código
```bash
docker compose up --build -d
```

### Reiniciar apenas a aplicação
```bash
docker compose restart app
```

### Executar comandos dentro do container
```bash
# Acessar shell da aplicação
docker compose exec app bash

# Acessar MySQL
docker compose exec mysql mysql -u root -p
```

---

## 🐛 Troubleshooting

### Porta 8080 já está em uso
```bash
# Descobrir qual processo está usando
lsof -i :8080

# Ou altere a porta no compose.yaml:
ports:
  - "8081:8080"  # Acesse via localhost:8081
```

### Erro de conexão com o banco
```bash
# Verifique se o MySQL está saudável
docker compose ps

# Veja os logs do MySQL
docker compose logs mysql
```

### Reconstruir do zero
```bash
# Limpar tudo
docker compose down -v
docker rmi fly-api:local

# Subir novamente
docker compose up --build -d
```

---

## 📁 Estrutura do Projeto

```
Fly-API/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   ├── compose.yaml
│   ├── pom.xml
│   └── mvnw
└── README.md
```

---

## 🔧 Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 3.5.7**
- **MySQL (latest)**
- **Docker & Docker Compose**
- **Maven**

---

## 📝 Desenvolvimento Local (sem Docker)

Se preferir rodar localmente sem Docker:

1. Instale Java 25 e Maven
2. Configure MySQL local com as credenciais do `application.properties`
3. Execute:
```bash
cd app
./mvnw spring-boot:run
```
