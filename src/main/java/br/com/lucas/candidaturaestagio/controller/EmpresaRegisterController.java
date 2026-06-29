package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import br.com.lucas.candidaturaestagio.service.EmpresaService;
import br.com.lucas.candidaturaestagio.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/empresa")
public class EmpresaRegisterController {

    private final EmpresaRepository empresaRepository;
    private final EmpresaService empresaService;
    private final MailService mailService;

    public EmpresaRegisterController(EmpresaRepository empresaRepository,
                                    EmpresaService empresaService,
                                    MailService mailService) {
        this.empresaRepository = empresaRepository;
        this.empresaService = empresaService;
        this.mailService = mailService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresa_register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("empresa") Empresa empresa,
                          BindingResult result,
                          HttpServletRequest request,
                          Model model) {
        if (empresa.getEmail() != null && empresaRepository.findByEmail(empresa.getEmail()).isPresent()) {
            result.rejectValue("email", "duplicate.email", "Já existe uma empresa cadastrada com este e-mail");
        }
        if (empresa.getCnpj() != null && empresaRepository.findByCnpj(empresa.getCnpj()).isPresent()) {
            result.rejectValue("cnpj", "duplicate.cnpj", "Já existe uma empresa cadastrada com este CNPJ");
        }
        if (result.hasErrors()) {
            return "empresa_register";
        }
        
        // Usar service para criptografar senha
        empresaService.registrar(empresa);
        
        // Enviar email de boas-vindas com template HTML
        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("nome", empresa.getNome());
        mailService.sendHtmlEmail(empresa.getEmail(), "Bem-vindo ao Sistema de Estágios", "email/empresa-bem-vindo", variables);
        
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("empresaId", empresa.getId());
        return "redirect:/empresa/vagas";
    }
}
