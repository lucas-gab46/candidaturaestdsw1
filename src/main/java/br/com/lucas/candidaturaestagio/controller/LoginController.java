package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Administrador;
import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.AdministradorRepository;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import br.com.lucas.candidaturaestagio.service.AdministradorService;
import br.com.lucas.candidaturaestagio.service.CandidatoService;
import br.com.lucas.candidaturaestagio.service.EmpresaService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
public class LoginController {

    private final AdministradorRepository administradorRepository;
    private final EmpresaRepository empresaRepository;
    private final AdministradorService administradorService;
    private final EmpresaService empresaService;
    private final CandidatoService candidatoService;

    public LoginController(AdministradorRepository administradorRepository,
                          EmpresaRepository empresaRepository,
                          AdministradorService administradorService,
                          EmpresaService empresaService,
                          CandidatoService candidatoService) {
        this.administradorRepository = administradorRepository;
        this.empresaRepository = empresaRepository;
        this.administradorService = administradorService;
        this.empresaService = empresaService;
        this.candidatoService = candidatoService;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(name = "role", required = false, defaultValue = "candidate") String role,
                            Model model) {
        model.addAttribute("selectedRole", role);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(name = "username", required = false) String username,
                        @RequestParam(name = "password", required = false) String password,
                        @RequestParam(name = "role", required = false, defaultValue = "candidate") String role,
                        HttpServletRequest request, Model model) {
        if ((username == null || username.isBlank()) && request.getParameter("email") != null) {
            username = request.getParameter("email");
        }
        if ((password == null || password.isBlank()) && request.getParameter("senha") != null) {
            password = request.getParameter("senha");
        }
        model.addAttribute("selectedRole", role);
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("errorMessageCode", "erro.campoObrigatorio");
            return "login";
        }

        if ("candidate".equalsIgnoreCase(role)) {
            if (candidatoService.validarCredenciais(username, password)) {
                Optional<Candidato> candidatoOpt = candidatoService.obterPorEmail(username);
                if (candidatoOpt.isPresent()) {
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("candidatoId", candidatoOpt.get().getId());
                    newSession.setAttribute("userRole", "candidate");
                    return "redirect:/candidato/candidaturas";
                }
            }
        } else {
            if (empresaService.validarCredenciais(username, password)) {
                Optional<Empresa> empresaOpt = empresaService.obterPorEmail(username);
                if (empresaOpt.isPresent()) {
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("empresaId", empresaOpt.get().getId());
                    newSession.setAttribute("userRole", "company");
                    return "redirect:/empresa/vagas";
                }
            }
            if (administradorService.validarCredenciais(username, password)) {
                Optional<Administrador> adminOpt = administradorService.obterPorEmail(username);
                if (adminOpt.isPresent()) {
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("admin", true);
                    newSession.setAttribute("adminId", adminOpt.get().getId());
                    newSession.setAttribute("userRole", "admin");
                    return "redirect:/admin/empresas";
                }
            }
        }

        model.addAttribute("errorMessageCode", "erro.invalidoCredenciais");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
