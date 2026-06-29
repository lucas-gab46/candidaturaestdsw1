package br.com.lucas.candidaturaestagio;

import br.com.lucas.candidaturaestagio.model.Administrador;
import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.model.Candidatura;
import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.model.ModalidadeTrabalho;
import br.com.lucas.candidaturaestagio.model.Sexo;
import br.com.lucas.candidaturaestagio.model.StatusCandidatura;
import br.com.lucas.candidaturaestagio.model.StatusVaga;
import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.AdministradorRepository;
import br.com.lucas.candidaturaestagio.repository.CandidatoRepository;
import br.com.lucas.candidaturaestagio.repository.CandidaturaRepository;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import br.com.lucas.candidaturaestagio.service.AdministradorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class CandidaturaEstagioApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CandidaturaEstagioApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CandidaturaEstagioApplication.class, args);
    }

    @Bean
    CommandLineRunner executarCrudInicial(
            AdministradorRepository administradorRepository,
            AdministradorService administradorService,
            CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository,
            VagaRepository vagaRepository,
            CandidaturaRepository candidaturaRepository
    ) {
        return args -> {
            System.out.println("=== Iniciando carga e CRUD de exemplo da T5 ===");

            // CREATE: administrador inicial exigido no projeto.
            Administrador administrador = new Administrador("admin@sistema.com", "admin123");
            administradorService.registrar(administrador);

            // CREATE: empresa, candidato, vaga e candidatura.
            Empresa empresa = new Empresa(
                    "rh@techestagios.com",
                    "empresa123",
                    "12.345.678/0001-90",
                    "Tech Estágios",
                    "(19) 99999-1111",
                    "Tecnologia",
                    "Campinas",
                    "Empresa focada em desenvolvimento de sistemas web."
            );
            empresaRepository.save(empresa);

            Candidato candidato = new Candidato(
                    "lucas@email.com",
                    "lucas123",
                    "123.456.789-00",
                    "Lucas Santos",
                    "(19) 98888-2222",
                    Sexo.MASCULINO,
                    LocalDate.of(2004, 6, 1),
                    "Análise e Desenvolvimento de Sistemas",
                    "5º semestre",
                    "Campinas",
                    "Currículo cadastrado em texto para exemplo da T5."
            );
            candidatoRepository.save(candidato);

            Vaga vaga = new Vaga(
                    empresa,
                    "Estágio em Desenvolvimento Web",
                    "Atuação com Java, Spring MVC, Thymeleaf e banco de dados relacional.",
                    "Tecnologia",
                    "Conhecimentos básicos em Java, HTML, CSS e SQL.",
                    30,
                    ModalidadeTrabalho.HIBRIDO,
                    new BigDecimal("1200.00"),
                    "Vale transporte e possibilidade de efetivação.",
                    "Campinas",
                    LocalDate.now().plusDays(30),
                    StatusVaga.ABERTA
            );
            vagaRepository.save(vaga);

            Vaga vaga2 = new Vaga(
                    empresa,
                    "Estágio em Desenvolvimento Movel",
                    "Atuação com Java, Flutter e React Native.",
                    "Tecnologia",
                    "Conhecimentos básicos em Front-end e Mobile.",
                    30,
                    ModalidadeTrabalho.PRESENCIAL,
                    new BigDecimal("1500.00"),
                    "Vale transporte e plano de desenvolvimento.",
                    "São Carlos",
                    LocalDate.now().plusDays(30),
                    StatusVaga.ABERTA
            );
            vagaRepository.save(vaga2);

            Vaga vaga3 = new Vaga(
                    empresa,
                    "Estágio em Desenvolvimento Front-end",
                    "Atuação com JavaScript.",
                    "Tecnologia",
                    "Conhecimentos básicos em Front-end.",
                    30,
                    ModalidadeTrabalho.PRESENCIAL,
                    new BigDecimal("3500.00"),
                    "Vale transporte e possibilidade de efetivação.",
                    "São Carlos",
                    LocalDate.now().plusDays(30),
                    StatusVaga.FECHADA
            );
            vagaRepository.save(vaga3);


            

            Candidatura candidatura = new Candidatura(
                    candidato,
                    vaga,
                    "Tenho interesse na vaga e já estudo Java com Spring.",
                    candidato.getCurriculo(),
                    StatusCandidatura.EM_ANALISE
            );
            candidaturaRepository.save(candidatura);

            // READ: listagem de dados persistidos.
            System.out.println("Administradores cadastrados: " + administradorRepository.count());
            System.out.println("Candidatos cadastrados: " + candidatoRepository.count());
            System.out.println("Empresas cadastradas: " + empresaRepository.count());
            System.out.println("Vagas cadastradas: " + vagaRepository.count());
            System.out.println("Candidaturas cadastradas: " + candidaturaRepository.count());

            vagaRepository.findAll().forEach(v ->
                    System.out.println("Vaga: " + v.getId() + " - " + v.getTitulo() + " - " + v.getStatus())
            );

            // UPDATE: atualização de entidades.
            candidato.setPeriodo("6º semestre");
            candidatoRepository.save(candidato);

            candidatura.setStatus(StatusCandidatura.APROVADO);
            candidatura.setLinkVideoconferencia("https://meet.google.com/exemplo-t5");
            candidatura.setDataEntrevista(LocalDate.now().plusDays(7));
            candidaturaRepository.save(candidatura);

            System.out.println("Candidato atualizado para: " + candidato.getPeriodo());
            System.out.println("Candidatura atualizada para: " + candidatura.getStatus());

            // DELETE: exemplo real de remoção em uma entidade temporária.
            Candidato candidatoTemporario = new Candidato(
                    "temporario@email.com",
                    "temp1234",
                    "987.654.321-00",
                    "Candidato Temporário",
                    "(19) 97777-3333",
                    Sexo.OUTRO,
                    LocalDate.of(2002, 3, 15),
                    "Ciência da Computação",
                    "3º semestre",
                    "Sumaré",
                    "Currículo temporário."
            );
            candidatoRepository.save(candidatoTemporario);
            candidatoRepository.delete(candidatoTemporario);

            System.out.println("CRUD executado com sucesso!");
            System.out.println("Acesse: http://localhost:8080");
            System.out.println("H2 Console: http://localhost:8080/h2-console");
            System.out.println("JDBC URL: jdbc:h2:mem:candidaturadb");
        };
    }
}
