package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.model.Candidatura;
import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.CandidatoRepository;
import br.com.lucas.candidaturaestagio.repository.CandidaturaRepository;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/candidato")
public class CandidatoController {

    private final CandidatoRepository candidatoRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;

    public CandidatoController(CandidatoRepository candidatoRepository,
                               CandidaturaRepository candidaturaRepository,
                               VagaRepository vagaRepository) {
        this.candidatoRepository = candidatoRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.vagaRepository = vagaRepository;
    }

    @ModelAttribute("sexos")
    public String[] sexos() {
        return new String[]{"MASCULINO", "FEMININO", "OUTRO"};
    }

    @GetMapping("/login")
    public String loginForm() {
        return "candidato_login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpServletRequest request,
                        Model model) {
        Optional<Candidato> candidatoOpt = candidatoRepository.findByEmail(email);
        if (candidatoOpt.isPresent() && candidatoOpt.get().getSenha().equals(senha)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("candidatoId", candidatoOpt.get().getId());
            return "redirect:/candidato/candidaturas";
        }
        model.addAttribute("error", "Credenciais inválidas");
        return "candidato_login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("candidato", new Candidato());
        return "candidato_register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("candidato") Candidato candidato,
                           BindingResult result,
                           HttpServletRequest request,
                           Model model) {
        if (candidato.getEmail() != null && candidatoRepository.findByEmail(candidato.getEmail()).isPresent()) {
            result.rejectValue("email", "duplicate.email", "Já existe um candidato cadastrado com este e-mail");
        }
        if (candidato.getCpf() != null && candidatoRepository.findByCpf(candidato.getCpf()).isPresent()) {
            result.rejectValue("cpf", "duplicate.cpf", "Já existe um candidato cadastrado com este CPF");
        }
        if (result.hasErrors()) {
            return "candidato_register";
        }
        candidatoRepository.save(candidato);
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("candidatoId", candidato.getId());
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
        if (result.hasErrors()) {
            model.addAttribute("vaga", vagaOpt.get());
            return "candidato_vaga_form";
        }
        candidatura.setCandidato(candidatoOpt.get());
        candidatura.setVaga(vagaOpt.get());
        candidaturaRepository.save(candidatura);
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

    private Long getCandidatoId(HttpSession session) {
        Object candidatoId = session.getAttribute("candidatoId");
        return candidatoId instanceof Long ? (Long) candidatoId : null;
    }
}
