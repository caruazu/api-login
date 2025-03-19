# API REST com login

![Spring Boot](https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logoColor=white&logo=spring)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

## Sobre

> Esse projeto é um exemplo de login com perfis de usuário que criei para estudar.

### Dependências

Esta aplicação foi construída com:

- linux kernel: 5.15
- linux Mint: 21
- Postgres: 12
- Maven: 3.9

As dependências acima não são gerenciadas de forma automática e não impedem necessariamente o uso de outras versões.

### Configuração

Utilize o arquivo `.env.exemple` como exemplo para suas variáveis locais. Faça sua cópia do arquivo e altere o nome deste para `.env`, ponha os dados de sua implementação.

### Inicialização

Certifique-se que seu ambiente possui as dependências do projeto e que o banco definido em .env já está criado.

Vá até a pasta `sistema-svo-pra-backend`.

Instale as dependências:
```bash
./mvnw clean install
```

Execute a aplicação:
```bash
./mvnw spring-boot:run
```

Use o [insomnia](https://insomnia.rest/) para importar o arquivo [requests.json](src/main/resources/requests/requests.json) em `src/main/resources/requests`. Nele estão as requisições para interagir com o projeto.

#### Docker

```bash
docker build -t caruazu/api-login .
```

```bash
docker run --env-file .env -p 8080:8080 caruazu/api-login
```

##### localmente

```bash
docker run --env-file .env --network host caruazu/api-login
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
- Validação de capcha

### Para implementar

- Testes automatizados
- Documentação automática
- Login com o google
