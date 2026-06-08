package br.com.lucas.candidaturaestagio.repository;

import br.com.lucas.candidaturaestagio.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {
    Optional<Candidato> findByEmail(String email);
    Optional<Candidato> findByCpf(String cpf);
}
