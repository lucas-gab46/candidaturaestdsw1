package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.model.Candidatura;
import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.CandidatoRepository;
import br.com.lucas.candidaturaestagio.repository.CandidaturaRepository;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import br.com.lucas.candidaturaestagio.service.CandidatoService;
import br.com.lucas.candidaturaestagio.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/candidato")
public class CandidatoController {

    private static final Logger logger = LoggerFactory.getLogger(CandidatoController.class);

    private final CandidatoRepository candidatoRepository;
    private final CandidatoService candidatoService;
    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;
    private final MailService mailService;

    public CandidatoController(CandidatoRepository candidatoRepository,
                               CandidatoService candidatoService,
                               CandidaturaRepository candidaturaRepository,
                               VagaRepository vagaRepository,
                               MailService mailService) {
        this.candidatoRepository = candidatoRepository;
        this.candidatoService = candidatoService;
        this.candidaturaRepository = candidaturaRepository;
        this.vagaRepository = vagaRepository;
        this.mailService = mailService;
    }

    @ModelAttribute("sexos")
    public String[] sexos() {
        return new String[]{"MASCULINO", "FEMININO", "OUTRO"};
    }

    @GetMapping("/login")
    public String loginForm() {
        return "redirect:/login?role=candidate";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpServletRequest request,
                        Model model) {
        return "redirect:/login?role=candidate";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("candidato", new Candidato());
        return "candidato_register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("candidato") Candidato candidato,
                           BindingResult result,
                           @RequestParam(name = "resumeFile", required = false) MultipartFile resumeFile,
                           HttpServletRequest request,
                           Model model) {
        logger.info("Candidato register attempt: email={}, cpf={}, resumeFilePresent={}",
                candidato.getEmail(), candidato.getCpf(), resumeFile != null && !resumeFile.isEmpty());
        if (candidato.getEmail() != null && candidatoRepository.findByEmail(candidato.getEmail()).isPresent()) {
            result.rejectValue("email", "duplicate.email", "Já existe um candidato cadastrado com este e-mail");
        }
        if (candidato.getCpf() != null && candidatoRepository.findByCpf(candidato.getCpf()).isPresent()) {
            result.rejectValue("cpf", "duplicate.cpf", "Já existe um candidato cadastrado com este CPF");
        }
        String uploadedResume = readResumeFile(resumeFile);
        if (!uploadedResume.isBlank()) {
            candidato.setCurriculo(uploadedResume);
        }
        if (result.hasErrors()) {
            return "candidato_register";
        }
        // Usar service para criptografar senha
        Candidato candidatoSalvo = candidatoService.registrar(candidato);
        logger.info("Candidato registered: id={} email={}", candidatoSalvo.getId(), candidatoSalvo.getEmail());
        
        // Enviar email de boas-vindas com template HTML
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("nome", candidatoSalvo.getNome());
        mailService.sendHtmlEmail(candidatoSalvo.getEmail(), "Bem-vindo ao Sistema de Estágios", "email/bem-vindo", variables);
        
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("candidatoId", candidatoSalvo.getId());
        return "redirect:/candidato/candidaturas";
    }

    @GetMapping("/candidaturas")
    public String minhasCandidaturas(HttpSession session,
                                     Model model,
                                     @RequestParam(required = false) String mensagem) {
        Long candidatoId = getCandidatoId(session);
        if (candidatoId == null) {
            return "redirect:/candidato/login";
        }
        List<Candidatura> candidaturas = candidaturaRepository.findByCandidatoId(candidatoId);
        model.addAttribute("candidaturas", candidaturas);
        if (mensagem != null) {
            model.addAttribute("mensagem", mensagem);
        }
        return "candidato_candidaturas";
    }

    @GetMapping("/vagas/{vagaId}/candidatar")
    public String iniciarCandidatura(@PathVariable Long vagaId,
                                     HttpSession session,
                                     Model model) {
        Long candidatoId = getCandidatoId(session);
        if (candidatoId == null) {
            return "redirect:/candidato/login";
        }
        if (candidaturaRepository.existsByCandidatoIdAndVagaId(candidatoId, vagaId)) {
            return "redirect:/candidato/candidaturas";
        }
        Optional<Vaga> vagaOpt = vagaRepository.findById(vagaId);
        if (vagaOpt.isEmpty()) {
            return "redirect:/vagas";
        }
        Optional<Candidato> candidatoOpt = candidatoRepository.findById(candidatoId);
        if (candidatoOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/candidato/login";
        }
        Candidatura candidatura = new Candidatura();
        candidatura.setCurriculo(candidatoOpt.get().getCurriculo());
        model.addAttribute("vaga", vagaOpt.get());
        model.addAttribute("candidatura", candidatura);
        return "candidato_vaga_form";
    }

    @PostMapping("/vagas/{vagaId}/candidatar")
    public String enviarCandidatura(@PathVariable Long vagaId,
                                    @Valid @ModelAttribute("candidatura") Candidatura candidatura,
                                    BindingResult result,
                                    @RequestParam(name = "resumeFile", required = false) MultipartFile resumeFile,
                                    HttpSession session,
                                    Model model) {
        Long candidatoId = getCandidatoId(session);
        if (candidatoId == null) {
            return "redirect:/candidato/login";
        }
        Optional<Vaga> vagaOpt = vagaRepository.findById(vagaId);
        Optional<Candidato> candidatoOpt = candidatoRepository.findById(candidatoId);
        if (vagaOpt.isEmpty() || candidatoOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/candidato/login";
        }
        if (candidaturaRepository.existsByCandidatoIdAndVagaId(candidatoId, vagaId)) {
            return "redirect:/candidato/candidaturas";
        }
        String uploadedResume = readResumeFile(resumeFile);
        if (!uploadedResume.isBlank()) {
            candidatura.setCurriculo(uploadedResume);
        }
        if (result.hasErrors()) {
            model.addAttribute("vaga", vagaOpt.get());
            return "candidato_vaga_form";
        }
        Candidato candidato = candidatoOpt.get();
        Vaga vaga = vagaOpt.get();
        candidatura.setCandidato(candidato);
        candidatura.setVaga(vaga);
        candidaturaRepository.save(candidatura);
        mailService.sendEmail(candidato.getEmail(), "Candidatura recebida: " + vaga.getTitulo(),
                "Olá " + candidato.getNome() + ",\n\nSua candidatura para a vaga '" + vaga.getTitulo() + "' foi recebida com sucesso.\n\nAtenciosamente,\nEquipe de Estágios");
        mailService.sendEmail(vaga.getEmpresa().getEmail(), "Nova candidatura para sua vaga",
                "Olá " + vaga.getEmpresa().getNome() + ",\n\nO candidato " + candidato.getNome() + " enviou uma candidatura para a vaga '" + vaga.getTitulo() + "'.\n\nVerifique seu painel e avalie o candidato.\n\nAtenciosamente,\nEquipe de Estágios");
        return "redirect:/candidato/vagas/" + vagaId + "/candidatar/sucesso";
    }

    @GetMapping("/vagas/{vagaId}/candidatar/sucesso")
    public String candidaturaSucesso(@PathVariable Long vagaId,
                                     HttpSession session,
                                     Model model) {
        Long candidatoId = getCandidatoId(session);
        if (candidatoId == null) {
            return "redirect:/candidato/login";
        }
        Optional<Vaga> vagaOpt = vagaRepository.findById(vagaId);
        if (vagaOpt.isEmpty()) {
            return "redirect:/vagas";
        }
        model.addAttribute("vaga", vagaOpt.get());
        return "candidato_vaga_success";
    }

    private String readResumeFile(MultipartFile resumeFile) {
        if (resumeFile == null || resumeFile.isEmpty()) {
            return "";
        }
        try {
            return new String(resumeFile.getBytes(), StandardCharsets.UTF_8).trim();
        } catch (Exception ex) {
            return "";
        }
    }

    private Long getCandidatoId(HttpSession session) {
        Object candidatoId = session.getAttribute("candidatoId");
        return candidatoId instanceof Long ? (Long) candidatoId : null;
    }
}
