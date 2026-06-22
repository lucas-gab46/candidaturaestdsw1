package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;

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
    public String salvar(@Valid Empresa empresa,
                         BindingResult result,
                         Model model,
                         HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        Optional<Empresa> existingByEmail = empresa.getEmail() != null ? empresaRepository.findByEmail(empresa.getEmail()) : Optional.empty();
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(empresa.getId())) {
            result.rejectValue("email", "duplicate.email", "Já existe uma empresa cadastrada com este e-mail");
        }
        Optional<Empresa> existingByCnpj = empresa.getCnpj() != null ? empresaRepository.findByCnpj(empresa.getCnpj()) : Optional.empty();
        if (existingByCnpj.isPresent() && !existingByCnpj.get().getId().equals(empresa.getId())) {
            result.rejectValue("cnpj", "duplicate.cnpj", "Já existe uma empresa cadastrada com este CNPJ");
        }
        if (result.hasErrors()) {
            model.addAttribute("empresa", empresa);
            return "empresa_form";
        }
        try {
            empresaRepository.save(empresa);
        } catch (Exception ex) {
            model.addAttribute("error", "Não foi possível salvar a empresa. Verifique os dados e tente novamente.");
            model.addAttribute("empresa", empresa);
            return "empresa_form";
        }
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
