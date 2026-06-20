package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final VagaRepository vagaRepository;

    public HomeController(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("vagas", vagaRepository.findAll());
        return "index";
    }

    @GetMapping("/vagas")
    public String vagas(Model model) {
        List<Vaga> vagas = vagaRepository.findAll();
        if (!vagas.isEmpty()) {
            model.addAttribute("vaga1", vagas.get(0));
        }
        if (vagas.size() > 1) {
            model.addAttribute("vaga2", vagas.get(1));
        }
        if (vagas.size() > 2) {
            model.addAttribute("vaga3", vagas.get(2));
        }
        return "vagas";
    }
}
