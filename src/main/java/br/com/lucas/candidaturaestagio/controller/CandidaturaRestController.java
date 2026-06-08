package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Candidatura;
import br.com.lucas.candidaturaestagio.repository.CandidaturaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaRestController {

    private final CandidaturaRepository candidaturaRepository;

    public CandidaturaRestController(CandidaturaRepository candidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
    }

    @GetMapping
    public List<Candidatura> listar(
            @RequestParam(required = false) Long candidatoId,
            @RequestParam(required = false) Long vagaId
    ) {
        if (candidatoId != null) {
            return candidaturaRepository.findByCandidatoId(candidatoId);
        }
        if (vagaId != null) {
            return candidaturaRepository.findByVagaId(vagaId);
        }
        return candidaturaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidatura> buscarPorId(@PathVariable Long id) {
        return candidaturaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Candidatura cadastrar(@Valid @RequestBody Candidatura candidatura) {
        return candidaturaRepository.save(candidatura);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidatura> atualizar(@PathVariable Long id, @Valid @RequestBody Candidatura dados) {
        return candidaturaRepository.findById(id)
                .map(candidatura -> {
                    candidatura.setCandidato(dados.getCandidato());
                    candidatura.setVaga(dados.getVaga());
                    candidatura.setMensagemApresentacao(dados.getMensagemApresentacao());
                    candidatura.setCurriculo(dados.getCurriculo());
                    candidatura.setDataCandidatura(dados.getDataCandidatura());
                    candidatura.setStatus(dados.getStatus());
                    candidatura.setJustificativaRecusa(dados.getJustificativaRecusa());
                    candidatura.setDataEntrevista(dados.getDataEntrevista());
                    candidatura.setHorarioEntrevista(dados.getHorarioEntrevista());
                    candidatura.setLinkVideoconferencia(dados.getLinkVideoconferencia());
                    return ResponseEntity.ok(candidaturaRepository.save(candidatura));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!candidaturaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        candidaturaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
