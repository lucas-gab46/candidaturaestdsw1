# PLANO DE IMPLEMENTAÇÃO DETALHADO

---

## FASE 1: SEGURANÇA - Implementar Criptografia de Senhas e Autenticação

### 1.1 Adicionar Dependência Spring Security

**Arquivo**: `pom.xml`

Adicionar após `spring-boot-starter-thymeleaf`:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 1.2 Criar Configuração de Segurança

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/config/SecurityConfig.java`

```java
package br.com.lucas.candidaturaestagio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 1.3 Atualizar Candidato Model - Usar PasswordEncoder

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/model/Candidato.java`

No método `setPassword`, usar:
```java
private PasswordEncoder passwordEncoder;

@PrePersist
@PreUpdate
public void encodePassword() {
    if (this.senha != null && !this.senha.startsWith("$2a$")) {
        // Apenas encoda se não for já uma senha BCrypted
        // Usar injeção de dependência no service
    }
}
```

### 1.4 Criar CandidatoService com Criptografia

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/service/CandidatoService.java`

```java
package br.com.lucas.candidaturaestagio.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.repository.CandidatoRepository;

@Service
public class CandidatoService {
    
    private final CandidatoRepository candidatoRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CandidatoService(CandidatoRepository repo, PasswordEncoder encoder) {
        this.candidatoRepository = repo;
        this.passwordEncoder = encoder;
    }
    
    public Candidato registrar(Candidato candidato) {
        // Criptografar senha
        candidato.setSenha(passwordEncoder.encode(candidato.getSenha()));
        return candidatoRepository.save(candidato);
    }
    
    public boolean validarCredenciais(String email, String senha) {
        var candidatoOpt = candidatoRepository.findByEmail(email);
        if (candidatoOpt.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(senha, candidatoOpt.get().getSenha());
    }
}
```

### 1.5 Atualizar CandidatoController

Usar `CandidatoService` para criptografar senhas:

```java
@PostMapping("/login")
public String login(@RequestParam String email,
                    @RequestParam String senha,
                    HttpServletRequest request,
                    Model model) {
    if (candidatoService.validarCredenciais(email, senha)) {
        Optional<Candidato> candidatoOpt = candidatoRepository.findByEmail(email);
        // ... resto do código
    }
    // ...
}

@PostMapping("/register")
public String register(@Valid @ModelAttribute("candidato") Candidato candidato,
                       BindingResult result,
                       @RequestParam(name = "resumeFile", required = false) MultipartFile resumeFile,
                       HttpServletRequest request,
                       Model model) {
    // ...
    
    // Usar service
    candidatoService.registrar(candidato);
    
    // ...
}
```

---

## FASE 2: EMAIL - Ativar e Implementar Templates

### 2.1 Ativar Email em application.properties

**Arquivo**: `src/main/resources/application.properties`

Mude:
```properties
app.mail.enabled=false
```

Para:
```properties
app.mail.enabled=true
app.mail.from=sistema@estagios.local
app.mail.subject-prefix=[ESTÁGIOS]

# Configurar SMTP real (se disponível)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### 2.2 Criar Templates de Email

**Arquivo**: `src/main/resources/templates/email/bem-vindo.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #1f2937; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background: #f9fafb; }
        .footer { padding: 10px; text-align: center; color: #666; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Bem-vindo ao Sistema de Estágios</h1>
        </div>
        <div class="content">
            <p>Olá <strong th:text="${nome}"></strong>,</p>
            <p>Seu cadastro foi realizado com sucesso!</p>
            <p>Agora você pode:</p>
            <ul>
                <li>Acessar vagas disponíveis</li>
                <li>Candidatar-se às vagas de seu interesse</li>
                <li>Acompanhar o status de suas candidaturas</li>
            </ul>
            <p><a href="http://localhost:8080/candidato/login" style="background: #3b82f6; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Acessar Sistema</a></p>
        </div>
        <div class="footer">
            <p>© 2026 Sistema de Estágios - Todos os direitos reservados</p>
        </div>
    </div>
</body>
</html>
```

### 2.3 Atualizar MailService

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/service/MailService.java`

```java
package br.com.lucas.candidaturaestagio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    @Value("${app.mail.from:no-reply@estagios.local}")
    private String from;

    @Value("${app.mail.subject-prefix:[ESTÁGIOS]}")
    private String subjectPrefix;

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(String to, String subject, String text) {
        if (!enabled) {
            logger.info("Email disabled. Skipping send to {} with subject={}", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subjectPrefix + " " + subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Email sent to {} with subject={}", to, subject);
        } catch (MailException ex) {
            logger.error("Falha ao enviar e-mail para {}: {}", to, ex.getMessage(), ex);
        }
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!enabled) {
            logger.info("Email disabled. Skipping HTML email to {} with subject={}", to, subject);
            return;
        }
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subjectPrefix + " " + subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            logger.info("HTML Email sent to {} with subject={}", to, subject);
        } catch (MessagingException | MailException ex) {
            logger.error("Falha ao enviar e-mail HTML para {}: {}", to, ex.getMessage(), ex);
        }
    }
    
    public void enviarBemVindo(String email, String nome) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nome", nome);
        sendHtmlEmail(email, "Bem-vindo ao Sistema", "email/bem-vindo", variables);
    }
    
    public void enviarCandidaturaAprovada(String email, String nomeCandidato, String nomeVaga) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomeCandidato", nomeCandidato);
        variables.put("nomeVaga", nomeVaga);
        sendHtmlEmail(email, "Candidatura Aprovada", "email/candidatura-aprovada", variables);
    }
    
    public void enviarCandidaturaReprovada(String email, String nomeCandidato, String nomeVaga) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomeCandidato", nomeCandidato);
        variables.put("nomeVaga", nomeVaga);
        sendHtmlEmail(email, "Candidatura Não Aprovada", "email/candidatura-reprovada", variables);
    }
    
    public void enviarEntrevistaAgendada(String email, String nomeCandidato, String nomeVaga, String data, String hora, String link) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomeCandidato", nomeCandidato);
        variables.put("nomeVaga", nomeVaga);
        variables.put("data", data);
        variables.put("hora", hora);
        variables.put("link", link);
        sendHtmlEmail(email, "Entrevista Agendada", "email/entrevista-agendada", variables);
    }
}
```

---

## FASE 3: I18N - Internacionalização

### 3.1 Expandir messages.properties

**Arquivo**: `src/main/resources/messages.properties`

```properties
# Títulos e cabeçalhos
app.titulo=Sistema para Candidatura e Seleção de Vagas de Estágio
app.nome=Sistema de Estágios

# Listagem de Vagas
vaga.listagem=Listagem pública de vagas
vaga.titulo=Vagas Disponíveis
vaga.descricao=Encontre as melhores vagas de estágio
vaga.buscar=Buscar vagas
vaga.filtrar=Filtrar por
vaga.nao-encontradas=Nenhuma vaga encontrada

# Candidato
candidato.titulo=Candidato
candidato.cadastro=Cadastro de Candidato
candidato.login=Login de Candidato
candidato.email=E-mail
candidato.senha=Senha
candidato.cpf=CPF
candidato.nome=Nome
candidato.telefone=Telefone
candidato.sexo=Sexo
candidato.data-nascimento=Data de Nascimento
candidato.curso=Curso
candidato.periodo=Período
candidato.cidade=Cidade
candidato.curriculo=Currículo
candidato.upload-curriculo=Upload de Currículo
candidato.minhas-candidaturas=Minhas Candidaturas
candidato.sair=Sair da Conta

# Empresa
empresa.titulo=Empresa
empresa.cadastro=Cadastro de Empresa
empresa.login=Login Empresarial
empresa.email=E-mail
empresa.senha=Senha
empresa.cnpj=CNPJ
empresa.nome=Nome da Empresa
empresa.telefone=Telefone
empresa.descricao=Descrição
empresa.area-atuacao=Área de Atuação
empresa.cidade=Cidade
empresa.vagas=Minhas Vagas
empresa.candidaturas=Candidaturas Recebidas
empresa.sair=Sair da Conta

# Login/Autenticação
auth.login=Login
auth.password=Senha
auth.logout=Sair
auth.nao-autenticado=Você não está autenticado
auth.permissao-negada=Você não tem permissão para acessar este recurso
auth.credenciais-invalidas=E-mail ou senha inválidos
auth.lembrar-me=Lembrar-me neste computador
auth.esqueceu-senha=Esqueceu a senha?

# Formulário
form.enviar=Enviar
form.cancelar=Cancelar
form.salvar=Salvar
form.editar=Editar
form.deletar=Deletar
form.voltar=Voltar
form.obrigatorio=Campo obrigatório
form.email-invalido=E-mail inválido
form.senha-fraca=Senha deve ter no mínimo 6 caracteres
form.cpf-invalido=CPF inválido
form.cnpj-invalido=CNPJ inválido

# Mensagens de Sucesso
msg.cadastro-sucesso=Cadastro realizado com sucesso!
msg.login-sucesso=Login realizado com sucesso!
msg.atualizacao-sucesso=Dados atualizados com sucesso!
msg.candidatura-sucesso=Candidatura enviada com sucesso!
msg.email-enviado=E-mail enviado com sucesso!

# Mensagens de Erro
erro.generico=Ocorreu um erro ao processar sua solicitação.
erro.email-duplicado=Já existe uma conta cadastrada com este e-mail
erro.cpf-duplicado=Já existe uma conta cadastrada com este CPF
erro.cnpj-duplicado=Já existe uma conta cadastrada com este CNPJ
erro.candidatura-duplicada=Você já se candidatou para esta vaga
erro.vaga-nao-encontrada=Vaga não encontrada
erro.candidato-nao-encontrado=Candidato não encontrado
erro.empresa-nao-encontrada=Empresa não encontrada
erro.nao-autorizado=Você não está autorizado para esta ação

# Paginação
paginacao.primeiro=Primeiro
paginacao.anterior=Anterior
paginacao.proximo=Próximo
paginacao.ultimo=Último
paginacao.pagina=Página
paginacao.de=de

# Status
status.ativo=Ativo
status.inativo=Inativo
status.aprovado=Aprovado
status.reprovado=Reprovado
status.pendente=Pendente
status.em-analise=Em Análise
status.entrevista-agendada=Entrevista Agendada
```

### 3.2 Expandir messages_en.properties

**Arquivo**: `src/main/resources/messages_en.properties`

```properties
# Titles and headers
app.titulo=Internship Application and Selection System
app.nome=Internship System

# Job Listing
vaga.listagem=Public internship list
vaga.titulo=Available Positions
vaga.descricao=Find the best internship opportunities
vaga.buscar=Search positions
vaga.filtrar=Filter by
vaga.nao-encontradas=No positions found

# Candidate
candidato.titulo=Candidate
candidato.cadastro=Candidate Registration
candidato.login=Candidate Login
candidato.email=Email
candidato.senha=Password
candidato.cpf=CPF
candidato.nome=Full Name
candidato.telefone=Phone
candidato.sexo=Gender
candidato.data-nascimento=Date of Birth
candidato.curso=Course
candidato.periodo=Period
candidato.cidade=City
candidato.curriculo=Curriculum
candidato.upload-curriculo=Upload Resume
candidato.minhas-candidaturas=My Applications
candidato.sair=Logout

# Company
empresa.titulo=Company
empresa.cadastro=Company Registration
empresa.login=Company Login
empresa.email=Email
empresa.senha=Password
empresa.cnpj=CNPJ
empresa.nome=Company Name
empresa.telefone=Phone
empresa.descricao=Description
empresa.area-atuacao=Area of Operation
empresa.cidade=City
empresa.vagas=My Positions
empresa.candidaturas=Received Applications
empresa.sair=Logout

# Login/Authentication
auth.login=Login
auth.password=Password
auth.logout=Logout
auth.nao-autenticado=You are not authenticated
auth.permissao-negada=You do not have permission to access this resource
auth.credenciais-invalidas=Invalid email or password
auth.lembrar-me=Remember me on this computer
auth.esqueceu-senha=Forgot your password?

# Form
form.enviar=Submit
form.cancelar=Cancel
form.salvar=Save
form.editar=Edit
form.deletar=Delete
form.voltar=Back
form.obrigatorio=Required field
form.email-invalido=Invalid email
form.senha-fraca=Password must have at least 6 characters
form.cpf-invalido=Invalid CPF
form.cnpj-invalido=Invalid CNPJ

# Success Messages
msg.cadastro-sucesso=Registration successful!
msg.login-sucesso=Login successful!
msg.atualizacao-sucesso=Data updated successfully!
msg.candidatura-sucesso=Application sent successfully!
msg.email-enviado=Email sent successfully!

# Error Messages
erro.generico=An error occurred while processing your request.
erro.email-duplicado=An account with this email already exists
erro.cpf-duplicado=An account with this CPF already exists
erro.cnpj-duplicado=An account with this CNPJ already exists
erro.candidatura-duplicada=You have already applied for this position
erro.vaga-nao-encontrada=Position not found
erro.candidato-nao-encontrado=Candidate not found
erro.empresa-nao-encontrada=Company not found
erro.nao-autorizado=You are not authorized for this action

# Pagination
paginacao.primeiro=First
paginacao.anterior=Previous
paginacao.proximo=Next
paginacao.ultimo=Last
paginacao.pagina=Page
paginacao.de=of

# Status
status.ativo=Active
status.inativo=Inactive
status.aprovado=Approved
status.reprovado=Rejected
status.pendente=Pending
status.em-analise=Under Review
status.entrevista-agendada=Interview Scheduled
```

### 3.3 Criar WebConfig para I18n

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/config/WebConfig.java`

```java
package br.com.lucas.candidaturaestagio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale("pt", "BR"));
        resolver.setCookieName("lang");
        resolver.setCookieMaxAge(60 * 60 * 24 * 365); // 1 ano
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
```

---

## FASE 4: Validação e Máscaras

### 4.1 Adicionar Dependência para Máscaras

**Arquivo**: `pom.xml`

```xml
<!-- Para validação adicional -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
</dependency>
```

### 4.2 Criar Validators Customizados

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/validation/TelefoneValidator.java`

```java
package br.com.lucas.candidaturaestagio.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public void initialize(ValidTelefone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        // Remove caracteres especiais
        String cleaned = value.replaceAll("[^0-9]", "");
        // Telefone deve ter 10 ou 11 dígitos
        return cleaned.length() == 10 || cleaned.length() == 11;
    }
}
```

### 4.3 Adicionar Anotação de Validação

**Arquivo**: `src/main/java/br/com/lucas/candidaturaestagio/validation/ValidTelefone.java`

```java
package br.com.lucas.candidaturaestagio.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TelefoneValidator.class)
@Documented
public @interface ValidTelefone {
    String message() default "Telefone inválido. Use formato (11) 99999-9999";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### 4.4 Adicionar JavaScript para Máscaras

**Arquivo**: `src/main/resources/static/js/masks.js`

```javascript
// Máscara para telefone
function maskPhone(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length > 0) {
        if (value.length <= 10) {
            value = value.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
        } else {
            value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
        }
    }
    input.value = value;
}

// Máscara para CPF
function maskCPF(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length > 0) {
        value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }
    input.value = value;
}

// Máscara para CNPJ
function maskCNPJ(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length > 0) {
        value = value.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
    }
    input.value = value;
}

// Inicializar máscaras quando página carrega
document.addEventListener('DOMContentLoaded', function() {
    const phoneInputs = document.querySelectorAll('input[type="tel"], input[id*="telefone"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function() { maskPhone(this); });
    });
    
    const cpfInputs = document.querySelectorAll('input[id*="cpf"]');
    cpfInputs.forEach(input => {
        input.addEventListener('input', function() { maskCPF(this); });
    });
    
    const cnpjInputs = document.querySelectorAll('input[id*="cnpj"]');
    cnpjInputs.forEach(input => {
        input.addEventListener('input', function() { maskCNPJ(this); });
    });
});
```

---

## PRÓXIMOS PASSOS

Comece pela **FASE 1** (Segurança) pois é crítico. Depois passe para a **FASE 2** (Email).

Cada fase deve levar:
- Fase 1: 2-3 horas
- Fase 2: 2-3 horas
- Fase 3: 2-3 horas
- Fase 4: 2 horas
- Fase 5: 3-4 horas

**Total estimado: 12-17 horas de desenvolvimento**
