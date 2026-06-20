package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Profissional;
import br.com.lucas.candidaturaestagio.repository.ProfissionalRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin/profissionais")
public class ProfissionalController {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalController(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    private boolean isAdmin(HttpSession session) {
        Object a = session.getAttribute("admin");
        return a != null && Boolean.TRUE.equals(a);
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("profissionais", profissionalRepository.findAll());
        return "profissionais";
    }

    @GetMapping("/novo")
    public String novo(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("profissional", new Profissional());
        return "profissional_form";
    }

    @PostMapping
    public String salvar(Profissional profissional, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        profissionalRepository.save(profissional);
        return "redirect:/admin/profissionais";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        Optional<Profissional> p = profissionalRepository.findById(id);
        if (p.isPresent()) {
            model.addAttribute("profissional", p.get());
            return "profissional_form";
        }
        return "redirect:/admin/profissionais";
    }

    @GetMapping("/{id}/apagar")
    public String apagar(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        profissionalRepository.deleteById(id);
        return "redirect:/admin/profissionais";
    }
}
