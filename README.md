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

- Java 17+
- Maven 3.8+
- Docker + Docker Compose
- Git

## Instalação Rápida

### 1. Clonar o repositório

```bash
git clone https://github.com/Emyllio87/projeto-clinica-beleza-total.git
cd projeto-clinica-beleza-total
```

### 2. Iniciar infraestrutura (PostgreSQL, Redis, Evolution API)

```bash
docker-compose up -d postgres redis evolution-api
```

### 3. Compilar e executar o backend

```bash
cd backend
mvn clean package -DskipTests
java -jar target/*.jar
```

Ou para desenvolvimento:

```bash
java -jar target/*.jar --spring.profiles.active=dev
```

### 4. Acessar o sistema

- **Homepage**: http://localhost:8080
- **Login**: http://localhost:8080/login.html
- **Dashboard**: http://localhost:8080/dashboard.html
- **Swagger**: http://localhost:8080/swagger-ui.html

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `DB_USERNAME` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | `postgres` |
| `JWT_SECRET` | Chave secreta JWT (Base64) | (definido no properties) |
| `WHATSAPP_API_URL` | URL da Evolution API | `http://localhost:8081` |
| `WHATSAPP_API_KEY` | Chave da API WhatsApp | (definido no properties) |
| `WHATSAPP_HABILITADO` | Habilitar WhatsApp | `true` |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring | `dev` |

## Usuários Padrão (DataInitializer)

Os seguintes usuários são criados automaticamente no primeiro acesso:

| Nome | Email | Perfil | Senha |
|------|-------|--------|-------|
| Silvana da Costa | silvana@belezatotal.com.br | ADMIN | (gerada automaticamente - ver console) |
| Ana Beatriz | ana@belezatotal.com.br | ESTETICISTA_ADMINISTRADORA | (gerada automaticamente) |
| Carlos Eduardo | carlos@belezatotal.com.br | RECEPCIONISTA | (gerada automaticamente) |
| Fernanda Lima | fernanda@belezatotal.com.br | FUNCIONARIO | (gerada automaticamente) |
| Dr. Rafael Souza | rafael@belezatotal.com.br | FUNCIONARIO | (gerada automaticamente) |

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
├── docker-compose.yml
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
