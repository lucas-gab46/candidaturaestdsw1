package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        model.addAttribute("vagas", vagaRepository.findAll());
        return "vagas";
    }
}
