package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.CandidaturaRepository;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final VagaRepository vagaRepository;
    private final CandidaturaRepository candidaturaRepository;

    public HomeController(VagaRepository vagaRepository, CandidaturaRepository candidaturaRepository) {
        this.vagaRepository = vagaRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("vagas", vagaRepository.findAll());
        return "index";
    }

    @GetMapping("/vagas")
    public String vagas(Model model, HttpSession session) {
        List<Vaga> vagas = vagaRepository.findAll();
        model.addAttribute("vagas", vagas);

        Long candidatoId = getCandidatoId(session);
        if (candidatoId != null) {
            Map<Long, Boolean> vagasInscritas = new HashMap<>();
            candidaturaRepository.findByCandidatoId(candidatoId)
                    .forEach(candidatura -> vagasInscritas.put(candidatura.getVaga().getId(), true));
            model.addAttribute("vagasInscritas", vagasInscritas);
        }

        return "vagas";
    }

    private Long getCandidatoId(HttpSession session) {
        Object candidatoId = session.getAttribute("candidatoId");
        return candidatoId instanceof Long ? (Long) candidatoId : null;
    }
}
