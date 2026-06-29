# CHECKLIST DE IMPLEMENTAÇÃO

---

## 📋 VERIFICAÇÃO RÁPIDA DAS FUNCIONALIDADES

### ✅ FUNCIONALIDADES IMPLEMENTADAS

- [x] Login de Candidato (parcial - sem criptografia)
- [x] Login de Empresa (parcial - sem criptografia)
- [x] Login de Administrador (parcial - sem criptografia)
- [x] Registro de Candidato (com upload de currículo)
- [x] Registro de Empresa
- [x] Listagem de Vagas
- [x] Formulário de Candidatura
- [x] Avaliação de Candidaturas (Empresa)
- [x] Agendamento de Entrevistas
- [x] Modelo de dados (JPA/Hibernate)
- [x] Banco de Dados (H2 em memória)
- [x] Validação básica (annotations)

### ❌ FUNCIONALIDADES NÃO IMPLEMENTADAS OU INCOMPLETAS

- [ ] **Criptografia de Senhas** (BCrypt) - CRÍTICO
- [ ] **Spring Security** - Autenticação centralizada - CRÍTICO
- [ ] **Interceptor/Filtro de Autenticação** - Validar acesso a URLs - CRÍTICO
- [ ] **Controle de Acesso** (por papel/role) - CRÍTICO
- [ ] **Email Ativo** - `app.mail.enabled=true` - ALTO
- [ ] **Templates HTML de Email** - ALTO
- [ ] **Envio de Email em Eventos** - ALTO
- [ ] **I18n Funcional** (em todos templates) - ALTO
- [ ] **Seletor de Idioma** - MÉDIO
- [ ] **Máscara de Telefone** - MÉDIO
- [ ] **Validação de CPF/CNPJ** (dígitos verificadores) - MÉDIO
- [ ] **Validação de Força de Senha** - MÉDIO
- [ ] **Recuperação de Senha** - BAIXO
- [ ] **Confirmação de Email** - BAIXO
- [ ] **Melhorias de UI/UX** - BAIXO

---

## 🚀 PLANO SEMANA 1 (PRIORITÁRIO)

### ✅ DIA 1: Segurança Básica
- [ ] Adicionar Spring Security ao pom.xml
- [ ] Criar SecurityConfig com BCryptPasswordEncoder
- [ ] Criar CandidatoService com criptografia
- [ ] Atualizar CandidatoController para usar service
- [ ] Atualizar LoginController (Empresa e Admin)
- [ ] Criar serviços equivalentes para Empresa e Administrador
- [ ] Testar login com senhas criptografadas

**Tempo estimado: 2-3 horas**

---

### ✅ DIA 2: Interceptor e Controle de Acesso
- [ ] Criar AuthInterceptor para validar autenticação
- [ ] Implementar controle de acesso por papel (CANDIDATO, EMPRESA, ADMIN)
- [ ] Proteger URLs do candidato (`/candidato/*`)
- [ ] Proteger URLs da empresa (`/empresa/*`)
- [ ] Proteger URLs do admin (`/admin/*`)
- [ ] Criar página de erro 403 (acesso negado)
- [ ] Testar acesso não autorizado

**Tempo estimado: 2-3 horas**

---

### ✅ DIA 3: Email
- [ ] Ativar `app.mail.enabled=true`
- [ ] Criar templates HTML de email:
  - [ ] bem-vindo.html
  - [ ] candidatura-aprovada.html
  - [ ] candidatura-reprovada.html
  - [ ] entrevista-agendada.html
- [ ] Atualizar MailService com templates
- [ ] Adicionar chamadas de email em eventos:
  - [ ] Ao registrar candidato
  - [ ] Quando candidatura é aprovada
  - [ ] Quando candidatura é reprovada
  - [ ] Quando entrevista é agendada
- [ ] Testar envio de emails

**Tempo estimado: 3-4 horas**

---

### ✅ DIA 4: I18n (Internacionalização)
- [ ] Expandir messages.properties (50+ chaves)
- [ ] Expandir messages_en.properties (50+ chaves)
- [ ] Criar WebConfig com LocaleResolver
- [ ] Adicionar LocaleChangeInterceptor
- [ ] Atualizar todos templates HTML com `th:text="#{...}"`
  - [ ] index.html
  - [ ] login.html
  - [ ] candidato_login.html
  - [ ] candidato_register.html
  - [ ] empresa_register.html
  - [ ] empresa_form.html
  - [ ] candidato_candidaturas.html
  - [ ] empresa_candidaturas.html
- [ ] Criar seletor de idioma no header
- [ ] Testar mudança de idioma PT-BR ↔ EN

**Tempo estimado: 2-3 horas**

---

### ✅ DIA 5: Validação e Máscaras
- [ ] Criar ValidTelefone (anotação customizada)
- [ ] Criar TelefoneValidator
- [ ] Adicionar masks.js para máscaras no frontend
- [ ] Incluir masks.js em todos templates
- [ ] Validar telefone em Candidato
- [ ] Validar telefone em Empresa
- [ ] Adicionar validação de força de senha
- [ ] Testar máscaras em tempo real
- [ ] Testar validação no backend

**Tempo estimado: 2-3 horas**

---

## 📋 PLANO SEMANA 2 (MELHORIAS)

### ⭐ DIA 6: Testes
- [ ] Testar todos logins (Candidato, Empresa, Admin)
- [ ] Testar acesso não autorizado
- [ ] Testar envio de emails
- [ ] Testar mudança de idioma
- [ ] Testar máscaras
- [ ] Testar validação de dados
- [ ] Criar plano de testes

**Tempo estimado: 2-3 horas**

---

### ⭐ DIA 7: Layout e UI
- [ ] Adicionar Bootstrap 5
- [ ] Criar header responsivo
- [ ] Criar menu mobile
- [ ] Melhorar formulários
- [ ] Adicionar ícones (Font Awesome)
- [ ] Criar componentes UI (alertas, modais)
- [ ] Dark mode (opcional)

**Tempo estimado: 3-4 horas**

---

### ⭐ DIA 8: Funcionalidades Extras
- [ ] Recuperação de senha
- [ ] Confirmação de email
- [ ] Dashboard com estatísticas
- [ ] Filtro avançado de vagas
- [ ] Recomendação de vagas

**Tempo estimado: 4-5 horas**

---

## 🔍 INSTRUÇÕES PASSO A PASSO

### Para Implementar Segurança (Dia 1)

1. **Abra `pom.xml`** e localize a seção `<dependencies>`
2. **Adicione** a dependência Spring Security (ver PLANO_IMPLEMENTACAO.md)
3. **Crie** arquivo `SecurityConfig.java` (ver PLANO_IMPLEMENTACAO.md)
4. **Crie** arquivo `CandidatoService.java` (ver PLANO_IMPLEMENTACAO.md)
5. **Atualize** `CandidatoController.java` para usar o service
6. **Execute** `mvn clean install`
7. **Teste** fazendo login com um novo candidato

---

### Para Ativar Email (Dia 3)

1. **Abra** `application.properties`
2. **Mude** `app.mail.enabled=false` para `app.mail.enabled=true`
3. **Configure** SMTP (gmial, SendGrid, etc.)
4. **Crie** templates HTML em `src/main/resources/templates/email/`
5. **Atualize** `MailService.java` (ver PLANO_IMPLEMENTACAO.md)
6. **Atualize** `CandidatoController.register()` para chamar `mailService.enviarBemVindo()`
7. **Atualize** `EmpresaCandidaturaController.atualizar()` para enviar emails
8. **Teste** registrando um candidato

---

### Para Implementar I18n (Dia 4)

1. **Atualize** `messages.properties` (ver PLANO_IMPLEMENTACAO.md)
2. **Atualize** `messages_en.properties` (ver PLANO_IMPLEMENTACAO.md)
3. **Crie/Atualize** `WebConfig.java` (ver PLANO_IMPLEMENTACAO.md)
4. **Atualize** todos `.html` templates para usar `th:text="#{chave}"`
5. **Exemplo**: `<h1 th:text="#{candidato.cadastro}">Cadastro de Candidato</h1>`
6. **Teste** acessando `http://localhost:8080/?lang=en` para inglês

---

## 📊 MÉTRICAS DE SUCESSO

| Métrica | Atual | Alvo |
|---------|-------|------|
| Funcionalidades Implementadas | 65% | 95% |
| Senhas Criptografadas | 0% | 100% |
| Emails Enviados | 0% | 100% |
| I18n Funcional | 5% | 90% |
| Cobertura de Validação | 60% | 90% |
| Testes de Segurança | 0% | 100% |

---

## ⚠️ DEPENDÊNCIAS

Para implementar:
- [x] Maven 3.6+
- [x] Java 17+
- [x] Spring Boot 3.3.5+
- [x] Tomcat 11
- [ ] SMTP configurado (para email)
- [ ] IDE (VS Code, IntelliJ, etc.)

---

## 📞 SUPORTE E REFERÊNCIAS

- **Spring Security**: https://spring.io/projects/spring-security
- **Spring I18n**: https://spring.io/projects/spring-hateoas
- **Thymeleaf**: https://www.thymeleaf.org/
- **Bootstrap 5**: https://getbootstrap.com/
- **HTML Email**: https://mailtrap.io/blog/html-email-template/

---

**Última atualização**: 25 de Junho de 2026
**Status**: Pronto para implementação
**Tempo total estimado**: 12-20 horas
