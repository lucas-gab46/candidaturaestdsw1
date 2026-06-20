package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Administrador;
import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.AdministradorRepository;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AdministradorRepository administradorRepository;
    private final EmpresaRepository empresaRepository;

    public LoginController(AdministradorRepository administradorRepository, EmpresaRepository empresaRepository) {
        this.administradorRepository = administradorRepository;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(name = "username", required = false) String username,
                        @RequestParam(name = "password", required = false) String password,
                        HttpServletRequest request, Model model) {
        // Suporte legado: aceitar parâmetros 'email'/'senha' em português também
        if ((username == null || username.isBlank()) && request.getParameter("email") != null) {
            username = request.getParameter("email");
        }
        if ((password == null || password.isBlank()) && request.getParameter("senha") != null) {
            password = request.getParameter("senha");
        }
        if (username == null || password == null) {
            model.addAttribute("error", "Preencha usuário e senha");
            return "login";
        }
        Empresa empresa = empresaRepository.findByEmail(username).orElse(null);
        if (empresa != null && empresa.getSenha().equals(password)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("empresaId", empresa.getId());
            return "redirect:/empresa/vagas";
        }
        Administrador admin = administradorRepository.findByEmail(username).orElse(null);
        if (admin != null && admin.getSenha().equals(password)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("admin", true);
            return "redirect:/admin/empresas";
        }
        model.addAttribute("error", "Credenciais inválidas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
