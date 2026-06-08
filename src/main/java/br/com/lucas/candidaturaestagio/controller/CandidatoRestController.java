package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.repository.CandidatoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/candidatos")
public class CandidatoRestController {

    private final CandidatoRepository candidatoRepository;

    public CandidatoRestController(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    @GetMapping
    public List<Candidato> listar() {
        return candidatoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidato> buscarPorId(@PathVariable Long id) {
        return candidatoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Candidato cadastrar(@Valid @RequestBody Candidato candidato) {
        return candidatoRepository.save(candidato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidato> atualizar(@PathVariable Long id, @Valid @RequestBody Candidato dados) {
        return candidatoRepository.findById(id)
                .map(candidato -> {
                    candidato.setEmail(dados.getEmail());
                    candidato.setSenha(dados.getSenha());
                    candidato.setCpf(dados.getCpf());
                    candidato.setNome(dados.getNome());
                    candidato.setTelefone(dados.getTelefone());
                    candidato.setSexo(dados.getSexo());
                    candidato.setDataNascimento(dados.getDataNascimento());
                    candidato.setCurso(dados.getCurso());
                    candidato.setPeriodo(dados.getPeriodo());
                    candidato.setCidade(dados.getCidade());
                    candidato.setCurriculo(dados.getCurriculo());
                    return ResponseEntity.ok(candidatoRepository.save(candidato));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!candidatoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        candidatoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
