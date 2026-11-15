# Clínica Beleza Total - Sistema de Gestão e Agendamento

Sistema completo para gestão de clínica de estética, incluindo agendamento online, controle de clientes, funcionários, procedimentos e notificações via WhatsApp.

## Funcionalidades

- **Homepage profissional** com design responsivo
- **Dashboard administrativo** com 10 seções
- **CRUD completo**: Clientes, Funcionários, Procedimentos, Usuários, Horários
- **Agendamento** com detecção de conflitos (pessimistic locking)
- **Notificações WhatsApp** via Evolution API
- **Lembretes automáticos** (scheduler a cada 15 min)
- **Relatórios e indicadores** com filtros por período
- **Minha Agenda** pessoal para cada funcionário
- **Controle de acesso** por perfis (Admin, Esteticista Administradora, Recepcionista, Funcionário)
- **Conformidade LGPD** com consentimento e anonimização de dados
- **API REST documentada** com Swagger/SpringDoc

## Tecnologias

- **Backend**: Java 17, Spring Boot 3.2.2, Spring Security, JPA/Hibernate
- **Frontend**: HTML5, CSS3, JavaScript vanilla
- **Banco**: PostgreSQL 16
- **Migrações**: Flyway
- **WhatsApp**: Evolution API
- **Cache**: Redis
- **Containerização**: Docker + Docker Compose

## Pré-requisitos

- Java 17+ (testado com Java 21)
- Maven 3.8+
- PostgreSQL 16+ instalado e rodando na máquina (porta 5432)
- Docker + Docker Compose (apenas para o WhatsApp/Evolution API — opcional)

No Debian/Ubuntu:

```bash
sudo apt install openjdk-21-jdk maven postgresql
```

## Rodando localmente (passo a passo)

### 1. Preparar o PostgreSQL nativo

```bash
sudo bash scripts/preparar-banco.sh
```

O script define a senha do usuário `postgres` como `postgres`, cria o banco da aplicação (`clinica_beleza_total`) e o banco da Evolution API (`evolution`). Para usar outra senha: `sudo DB_PASSWORD=minhaSenha bash scripts/preparar-banco.sh`.

Equivalente manual:

```bash
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';"
sudo -u postgres createdb clinica_beleza_total
sudo -u postgres createdb evolution
```

O `application.properties` já aponta para `jdbc:postgresql://localhost:5432/clinica_beleza_total` com usuário/senha `postgres`/`postgres`.

### 2. Compilar e executar o backend

```bash
cd backend
mvn clean package -DskipTests
WHATSAPP_HABILITADO=false java -jar target/clinica-beleza-total-1.0.0.jar
```

> **Atenção:** `mvn package` **sem** `-DskipTests` executa `PublicControllerTest`, que sobe o contexto completo do Spring e exige o banco já criado e acessível.

Em desenvolvimento também funciona:

```bash
WHATSAPP_HABILITADO=false mvn spring-boot:run
```

> `WHATSAPP_HABILITADO=false` coloca o envio em modo mock (apenas log no console). Sem isso, o sistema tenta falar com a Evolution API em `localhost:8081` a cada notificação, gerando erros e atraso de até 10s.

### 3. Acessar o sistema

- **Homepage**: http://localhost:8080
- **Login**: http://localhost:8080/login.html
- **Dashboard**: http://localhost:8080/dashboard.html
- **Swagger**: http://localhost:8080/swagger-ui.html

### 4. Fazer login

Na primeira execução, o `DataInitializer` cria procedimentos, funcionários, clientes, horários, agendamentos e usuários. As senhas são **aleatórias** e aparecem no console:

```text
=== USUÁRIOS CRIADOS (SENHAS AUTOMÁTICAS) ===
Admin: admin@belezatotal.com.br / xxxxxxxxxxxx
Silvana: silvana@belezatotal.com.br / xxxxxxxxxxxx
...
```

Se o banco já tiver usuários, as senhas não são recriadas. Para gerar novamente, use um banco vazio (`dropdb`/`createdb`) ou redefina a senha pelo dashboard.

### 5. WhatsApp real com Evolution API (Docker)

Opcional — só é necessário para enviar mensagens de verdade. O `docker-compose.yml` sobe apenas `redis` e `evolution-api`; o banco continua sendo o PostgreSQL nativo.

```bash
sudo bash scripts/instalar-docker.sh
# saia e entre na sessão novamente (grupo docker) ou rode: newgrp docker

docker compose up -d
docker compose logs -f evolution-api
```

Os containers usam `network_mode: host`, então a Evolution API acessa o Postgres nativo em `localhost:5432` e o Redis em `localhost:6379`. A API fica em `http://localhost:8081`.

Crie a instância e conecte o WhatsApp:

```bash
API_KEY="B22D5427010E-4E41-875E-B841A9497218"

curl -X POST http://localhost:8081/instance/create \
  -H "apikey: $API_KEY" -H "Content-Type: application/json" \
  -d '{"instanceName":"clinica","qrcode":true,"integration":"WHATSAPP-BAILEYS"}'

curl -H "apikey: $API_KEY" http://localhost:8081/instance/connect/clinica
```

Escaneie o QR code em **WhatsApp → Aparelhos conectados**. O nome da instância deve ser o mesmo de `whatsapp.phone-number-id` (padrão `clinica`). Depois reinicie o backend com `WHATSAPP_HABILITADO=true`.

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SPRING_DATASOURCE_URL` | URL JDBC do PostgreSQL (opcional) | `jdbc:postgresql://localhost:5432/clinica_beleza_total` |
| `DB_USERNAME` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | `postgres` |
| `JWT_SECRET` | Chave secreta JWT (Base64) | (definido no properties) |
| `WHATSAPP_API_URL` | URL da Evolution API | `http://localhost:8081` |
| `WHATSAPP_API_KEY` | Chave da API WhatsApp | (definido no properties) |
| `WHATSAPP_HABILITADO` | Habilitar WhatsApp (`false` = modo mock, apenas log no console) | `true` |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring | `dev` |

## Usuários Padrão (DataInitializer)

Os seguintes usuários são criados automaticamente no primeiro acesso:

| Nome | Email | Perfil | Senha |
|------|-------|--------|-------|
| Administrador | admin@belezatotal.com.br | ADMIN | (gerada automaticamente - ver console) |
| Silvana da Costa | silvana@belezatotal.com.br | ESTETICISTA_ADMINISTRADORA | (gerada automaticamente) |
| Fernanda Lima | fernanda@belezatotal.com.br | RECEPCIONISTA | (gerada automaticamente) |
| Ana Carolina Silva | ana@belezatotal.com.br | FUNCIONARIO | (gerada automaticamente) |
| Beatriz Santos | beatriz@belezatotal.com.br | FUNCIONARIO | (gerada automaticamente) |

> **Nota**: As senhas são geradas aleatoriamente e exibidas no console durante a inicialização.

## Estrutura do Projeto

```
projeto-clinica-beleza-total/
├── backend/
│   ├── src/main/java/com/clinicabelezatotal/
│   │   ├── config/          # Configurações (Security, Scheduler, DataInitializer)
│   │   ├── controller/      # Controllers REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Tratamento de exceções
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositórios
│   │   ├── security/        # JWT e Spring Security
│   │   └── service/         # Lógica de negócio
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-dev.properties
│   │   ├── application-prod.properties
│   │   ├── db/migration/    # Scripts Flyway
│   │   └── static/          # Frontend estático
│   └── Dockerfile
├── frontend/
│   ├── index.html           # Homepage
│   ├── login.html           # Página de login
│   ├── dashboard.html       # Painel administrativo
│   ├── css/style.css        # Estilos
│   ├── js/api.js            # Cliente API
│   ├── js/dashboard.js      # Lógica do dashboard
│   └── images/              # Imagens
├── docker-compose.yml         # Redis + Evolution API (WhatsApp)
├── scripts/
│   ├── instalar-docker.sh     # Instala Docker + Compose
│   └── preparar-banco.sh      # Cria bancos e senha no Postgres nativo
└── README.md
```

## Endpoints da API

### Públicos (sem autenticação)
- `GET /api/public/homepage` - Informações da homepage
- `GET /api/public/portfolio` - Catálogo de procedimentos
- `GET /api/public/funcionarios` - Lista de funcionários

### Autenticação
- `POST /api/usuarios/login` - Login (retorna JWT)

### Administrativos (requer autenticação)
- `GET/POST /api/clientes` - Gestão de clientes
- `GET/POST /api/funcionarios` - Gestão de funcionários
- `GET/POST /api/procedimentos` - Gestão de procedimentos
- `GET/POST /api/agendamentos` - Gestão de agendamentos
- `GET/POST /api/usuarios` - Gestão de usuários (Admin)
- `GET/POST /api/horarios` - Gestão de horários
- `GET /api/indicadores` - Relatórios e indicadores

## Licença

Desenvolvido por **Joao Eduardo** & **Emyllio Marques** - UEG Sistemas de Informação
