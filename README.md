# API REST com login

![Spring Boot](https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logoColor=white&logo=spring)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## Sobre

> Esse projeto é um exemplo de login com perfis de usuário que criei para estudar.

### Dependências

Esta aplicação foi construída com:

- Linux kernel: 5.15
- Docker: 28.0.1
- Linux Mint: 21
- Postgres: 12
- Maven: 3.9

As dependências acima não são gerenciadas de forma automática e não impedem necessariamente o uso de outras versões.

### Configuração

Utilize o arquivo `.env.exemple` como exemplo para suas variáveis locais. Faça sua cópia do arquivo e altere o nome deste para `.env`, ponha os dados de sua implementação.

### Inicialização

Certifique-se que seu ambiente possui as dependências do projeto e que o banco definido em .env já existe. Há duas opções de rodar o projeto:

#### Com Docker

Construa o container:
```bash
docker build -t caruazu/api-login .
```

Rode o container:
```bash
docker run --env-file .env --network host caruazu/api-login
```

#### Sem Docker

Instale o Maven.

Instale as dependências:
```bash
./mvnw clean install
```

Execute a aplicação:
```bash
./mvnw spring-boot:run
```

## Funcionalidades

### Implementado

- Autenticação via token JWT
- Perfis de usuário
- Número máximo de tentativas de senha até bloqueio temporário
- Confirmação e email
- Mudança de senha
- Validação de senha forte
- Validação de outros dados
- Padronização dos erros (http resposne)
- tratamento de exceções
- Organização das mensagens de erro
- Logging
- CORS
- Validação de CAPTCHA
- Desativar o CAPTCHA via variável de ambiente

### Para implementar

- padronização do erro de token inválido/expirado (java.lang.RuntimeException: Token expirado)
- Testes automatizados
- Documentação automática
- Login com o google
