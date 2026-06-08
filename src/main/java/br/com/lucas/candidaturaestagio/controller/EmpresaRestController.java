package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
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
@RequestMapping("/api/empresas")
public class EmpresaRestController {

    private final EmpresaRepository empresaRepository;

    public EmpresaRestController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        return empresaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Empresa cadastrar(@Valid @RequestBody Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @Valid @RequestBody Empresa dados) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setEmail(dados.getEmail());
                    empresa.setSenha(dados.getSenha());
                    empresa.setCnpj(dados.getCnpj());
                    empresa.setNome(dados.getNome());
                    empresa.setTelefone(dados.getTelefone());
                    empresa.setAreaAtuacao(dados.getAreaAtuacao());
                    empresa.setCidade(dados.getCidade());
                    empresa.setDescricao(dados.getDescricao());
                    return ResponseEntity.ok(empresaRepository.save(empresa));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!empresaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        empresaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
