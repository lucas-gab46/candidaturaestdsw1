package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.ModalidadeTrabalho;
import br.com.lucas.candidaturaestagio.model.StatusVaga;
import br.com.lucas.candidaturaestagio.model.Vaga;
import br.com.lucas.candidaturaestagio.repository.VagaRepository;
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
@RequestMapping("/api/vagas")
public class VagaRestController {

    private final VagaRepository vagaRepository;

    public VagaRestController(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    @GetMapping
    public List<Vaga> listar(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) ModalidadeTrabalho modalidade,
            @RequestParam(required = false) StatusVaga status
    ) {
        if (area != null && !area.isBlank()) {
            return vagaRepository.findByAreaAtuacaoIgnoreCase(area);
        }
        if (cidade != null && !cidade.isBlank()) {
            return vagaRepository.findByCidadeIgnoreCase(cidade);
        }
        if (modalidade != null) {
            return vagaRepository.findByModalidade(modalidade);
        }
        if (status != null) {
            return vagaRepository.findByStatus(status);
        }
        return vagaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> buscarPorId(@PathVariable Long id) {
        return vagaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Vaga cadastrar(@Valid @RequestBody Vaga vaga) {
        return vagaRepository.save(vaga);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vaga> atualizar(@PathVariable Long id, @Valid @RequestBody Vaga dados) {
        return vagaRepository.findById(id)
                .map(vaga -> {
                    vaga.setEmpresa(dados.getEmpresa());
                    vaga.setTitulo(dados.getTitulo());
                    vaga.setDescricao(dados.getDescricao());
                    vaga.setAreaAtuacao(dados.getAreaAtuacao());
                    vaga.setRequisitos(dados.getRequisitos());
                    vaga.setCargaHoraria(dados.getCargaHoraria());
                    vaga.setModalidade(dados.getModalidade());
                    vaga.setValorBolsa(dados.getValorBolsa());
                    vaga.setBeneficios(dados.getBeneficios());
                    vaga.setCidade(dados.getCidade());
                    vaga.setDataLimite(dados.getDataLimite());
                    vaga.setStatus(dados.getStatus());
                    return ResponseEntity.ok(vagaRepository.save(vaga));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!vagaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vagaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
