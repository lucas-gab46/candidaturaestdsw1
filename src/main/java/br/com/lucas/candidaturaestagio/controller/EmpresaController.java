package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin/empresas")
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    public EmpresaController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    private boolean isAdmin(HttpSession session) {
        Object a = session.getAttribute("admin");
        return a != null && Boolean.TRUE.equals(a);
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("empresas", empresaRepository.findAll());
        return "empresas";
    }

    @GetMapping("/novo")
    public String novo(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("empresa", new Empresa());
        return "empresa_form";
    }

    @PostMapping
    public String salvar(Empresa empresa, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        empresaRepository.save(empresa);
        return "redirect:/admin/empresas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        Optional<Empresa> e = empresaRepository.findById(id);
        if (e.isPresent()) {
            model.addAttribute("empresa", e.get());
            return "empresa_form";
        }
        return "redirect:/admin/empresas";
    }

    @GetMapping("/{id}/apagar")
    public String apagar(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        empresaRepository.deleteById(id);
        return "redirect:/admin/empresas";
    }
}
