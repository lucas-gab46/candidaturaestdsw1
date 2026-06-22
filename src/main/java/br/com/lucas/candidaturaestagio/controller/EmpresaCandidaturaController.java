package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Candidatura;
import br.com.lucas.candidaturaestagio.model.StatusCandidatura;
import br.com.lucas.candidaturaestagio.repository.CandidaturaRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empresa/candidaturas")
public class EmpresaCandidaturaController {

    private final CandidaturaRepository candidaturaRepository;

    public EmpresaCandidaturaController(CandidaturaRepository candidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
    }

    @ModelAttribute("statusOptions")
    public StatusCandidatura[] statusOptions() {
        return StatusCandidatura.values();
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        Long empresaId = getEmpresaId(session);
        if (empresaId == null) {
            return "redirect:/login";
        }
        List<Candidatura> candidaturas = candidaturaRepository.findByVagaEmpresaId(empresaId);
        model.addAttribute("candidaturas", candidaturas);
        return "empresa_candidaturas";
    }

    @GetMapping("/{id}/avaliar")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {
        Long empresaId = getEmpresaId(session);
        if (empresaId == null) {
            return "redirect:/login";
        }
        Optional<Candidatura> candidaturaOpt = candidaturaRepository.findById(id);
        if (candidaturaOpt.isEmpty() || candidaturaOpt.get().getVaga().getEmpresa().getId() == null
                || !candidaturaOpt.get().getVaga().getEmpresa().getId().equals(empresaId)) {
            return "redirect:/empresa/candidaturas";
        }
        model.addAttribute("candidatura", candidaturaOpt.get());
        return "empresa_candidatura_form";
    }

    @PostMapping("/{id}/avaliar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("candidatura") Candidatura dados,
                            BindingResult result,
                            HttpSession session,
                            Model model) {
        Long empresaId = getEmpresaId(session);
        if (empresaId == null) {
            return "redirect:/login";
        }
        Optional<Candidatura> candidaturaOpt = candidaturaRepository.findById(id);
        if (candidaturaOpt.isEmpty() || candidaturaOpt.get().getVaga().getEmpresa().getId() == null
                || !candidaturaOpt.get().getVaga().getEmpresa().getId().equals(empresaId)) {
            return "redirect:/empresa/candidaturas";
        }
        Candidatura candidatura = candidaturaOpt.get();
        if (result.hasErrors()) {
            model.addAttribute("candidatura", candidatura);
            return "empresa_candidatura_form";
        }
        candidatura.setStatus(dados.getStatus());
        candidatura.setJustificativaRecusa(dados.getJustificativaRecusa());
        candidatura.setDataEntrevista(dados.getDataEntrevista());
        candidatura.setHorarioEntrevista(dados.getHorarioEntrevista());
        candidatura.setLinkVideoconferencia(dados.getLinkVideoconferencia());
        candidaturaRepository.save(candidatura);
        return "redirect:/empresa/candidaturas";
    }

    private Long getEmpresaId(HttpSession session) {
        Object empresaId = session.getAttribute("empresaId");
        return empresaId instanceof Long ? (Long) empresaId : null;
    }
}
