package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.model.ModalidadeTrabalho;
import br.com.lucas.candidaturaestagio.model.StatusVaga;
import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empresa/vagas")
public class EmpresaVagaController {

    private final VagaRepository vagaRepository;
    private final EmpresaRepository empresaRepository;

    public EmpresaVagaController(VagaRepository vagaRepository, EmpresaRepository empresaRepository) {
        this.vagaRepository = vagaRepository;
        this.empresaRepository = empresaRepository;
    }

    private boolean isEmpresaLogged(HttpSession session) {
        return session.getAttribute("empresaId") != null;
    }

    private Long getEmpresaId(HttpSession session) {
        Object empresaId = session.getAttribute("empresaId");
        return empresaId instanceof Long ? (Long) empresaId : null;
    }

    @ModelAttribute("modalidades")
    public ModalidadeTrabalho[] modalidades() {
        return ModalidadeTrabalho.values();
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!isEmpresaLogged(session)) {
            return "redirect:/login";
        }
        Long empresaId = getEmpresaId(session);
        Optional<Empresa> empresaOpt = empresaRepository.findById(empresaId);
        if (empresaOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }
        Empresa empresa = empresaOpt.get();
        List<Vaga> vagas = vagaRepository.findByEmpresaId(empresaId);
        atualizarStatusVagas(vagas);
        model.addAttribute("empresa", empresa);
        model.addAttribute("vagas", vagas);
        return "empresa_vagas";
    }

    @GetMapping("/novo")
    public String novo(Model model, HttpSession session) {
        if (!isEmpresaLogged(session)) {
            return "redirect:/login";
        }
        Long empresaId = getEmpresaId(session);
        Optional<Empresa> empresaOpt = empresaRepository.findById(empresaId);
        if (empresaOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }
        model.addAttribute("empresa", empresaOpt.get());
        model.addAttribute("vaga", new Vaga());
        return "empresa_vaga_form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("vaga") Vaga vaga,
                         BindingResult result,
                         HttpSession session,
                         Model model) {
        if (!isEmpresaLogged(session)) {
            return "redirect:/login";
        }
        Long empresaId = getEmpresaId(session);
        Optional<Empresa> empresaOpt = empresaRepository.findById(empresaId);
        if (empresaOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            model.addAttribute("empresa", empresaOpt.get());
            return "empresa_vaga_form";
        }
        Empresa empresa = empresaOpt.get();
        vaga.setEmpresa(empresa);
        if (vaga.getStatus() == null) {
            vaga.setStatus(StatusVaga.ABERTA);
        }
        vagaRepository.save(vaga);
        return "redirect:/empresa/vagas";
    }

    private void atualizarStatusVagas(List<Vaga> vagas) {
        LocalDate hoje = LocalDate.now();
        for (Vaga vaga : vagas) {
            if (vaga.getDataLimite() != null && vaga.getDataLimite().isBefore(hoje)
                    && vaga.getStatus() == StatusVaga.ABERTA) {
                vaga.setStatus(StatusVaga.FECHADA);
                vagaRepository.save(vaga);
            }
        }
    }
}
