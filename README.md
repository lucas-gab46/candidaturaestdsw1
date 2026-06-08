# Sistema para Candidatura e Seleção de Vagas de Estágio - T5

Projeto Maven desenvolvido para a atividade **T5 - Usando JPA para acesso a banco de dados**.

Este projeto segue o domínio definido no PDF do sistema de candidatura e seleção de vagas de estágio e implementa a camada de persistência com **Spring MVC + Thymeleaf + Spring Data JPA**.

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Hibernate
- Bean Validation
- H2 Database
- Maven
- Empacotamento WAR
- Cargo Maven 3 Plugin
- Tomcat 11.x
- Tomcat 11.0.22 configurado no Cargo Maven Plugin

## Objetivo da T5

Implementar as classes responsáveis pelo acesso e manipulação do banco de dados relacional do sistema web escolhido.

Este projeto contém:

- Entidades JPA aderentes ao domínio do sistema.
- Repositories/DAOs usando Spring Data JPA.
- Classe principal com `@SpringBootApplication`.
- `CommandLineRunner` executando exemplos de CRUD no banco.
- REST API básica para consultar e manipular dados.
- Páginas Thymeleaf simples.
- Configuração Maven para gerar `.war`.
- Configuração Maven para executar no Tomcat 11.x via `mvn cargo:run`.

## Entidades implementadas

- Administrador
- Candidato
- Empresa
- Vaga
- Candidatura

## Relacionamentos principais

- Uma empresa pode possuir várias vagas.
- Uma vaga pertence a uma empresa.
- Um candidato pode realizar várias candidaturas.
- Uma candidatura pertence a um candidato e a uma vaga.

## Compatibilidade com Tomcat 11.x

O `pom.xml` está configurado com:

- `<packaging>war</packaging>`
- `spring-boot-starter-tomcat` com escopo `provided`
- dependências Jakarta Servlet/JSP/JSTL
- `maven-war-plugin`
- `cargo-maven3-plugin`
- container Cargo `tomcat11x`

A classe principal estende `SpringBootServletInitializer`, permitindo o deploy do `.war` em um Tomcat externo.

## Como compilar

Dentro da pasta do projeto, execute:

```bash
mvn clean package
```

O WAR será gerado em:

```text
target/candidatura-estagio-t5.war
```

## Como executar via Maven com Tomcat 11.x

Execute:

```bash
mvn cargo:run
```

O Cargo Maven Plugin baixa e inicializa o Tomcat 11.x configurado no `pom.xml`, faz o deploy do WAR e sobe a aplicação.

A aplicação ficará disponível em:

```text
http://localhost:8080
```

## Execução alternativa com Spring Boot

Também é possível executar para teste local com:

```bash
mvn spring-boot:run
```

## Console H2

Acesse:

```text
http://localhost:8080/h2-console
```

Use:

```text
JDBC URL: jdbc:h2:mem:candidaturadb
User: sa
Password: deixe em branco
```

## Endpoints REST principais

```text
GET    /api/administradores
GET    /api/candidatos
GET    /api/empresas
GET    /api/vagas
GET    /api/candidaturas
POST   /api/candidatos
POST   /api/empresas
POST   /api/vagas
POST   /api/candidaturas
PUT    /api/candidatos/{id}
PUT    /api/empresas/{id}
PUT    /api/vagas/{id}
PUT    /api/candidaturas/{id}
DELETE /api/candidatos/{id}
DELETE /api/empresas/{id}
DELETE /api/vagas/{id}
DELETE /api/candidaturas/{id}
```

## Dados criados automaticamente

Ao iniciar o sistema, o `CommandLineRunner` cria dados de exemplo:

- Administrador: `admin@sistema.com` / `admin123`
- Empresa: `Tech Estágios`
- Candidato: `Lucas Santos`
- Vaga: `Estágio em Desenvolvimento Web`
- Candidatura com status inicial `EM_ANALISE`, atualizada para `APROVADO`

O console também demonstra operações de CREATE, READ, UPDATE e DELETE.

## Entrega

Para a entrega da T5, suba todos os arquivos deste projeto para o GitHub e envie apenas a URL do repositório.
