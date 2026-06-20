package br.com.lucas.candidaturaestagio.repository;

import br.com.lucas.candidaturaestagio.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
}
