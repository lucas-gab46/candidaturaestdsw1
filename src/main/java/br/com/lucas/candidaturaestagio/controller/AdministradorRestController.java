package br.com.lucas.candidaturaestagio.controller;

import br.com.lucas.candidaturaestagio.model.Administrador;
import br.com.lucas.candidaturaestagio.repository.AdministradorRepository;
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
@RequestMapping("/api/administradores")
public class AdministradorRestController {

    private final AdministradorRepository administradorRepository;

    public AdministradorRestController(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @GetMapping
    public List<Administrador> listar() {
        return administradorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> buscarPorId(@PathVariable Long id) {
        return administradorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Administrador cadastrar(@Valid @RequestBody Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> atualizar(@PathVariable Long id, @Valid @RequestBody Administrador dados) {
        return administradorRepository.findById(id)
                .map(administrador -> {
                    administrador.setEmail(dados.getEmail());
                    administrador.setSenha(dados.getSenha());
                    return ResponseEntity.ok(administradorRepository.save(administrador));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!administradorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        administradorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
