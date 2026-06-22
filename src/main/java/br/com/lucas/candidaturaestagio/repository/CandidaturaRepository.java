package br.com.lucas.candidaturaestagio.repository;

import br.com.lucas.candidaturaestagio.model.Candidatura;
import br.com.lucas.candidaturaestagio.model.StatusCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    List<Candidatura> findByCandidatoId(Long candidatoId);
    List<Candidatura> findByVagaId(Long vagaId);
    List<Candidatura> findByVagaEmpresaId(Long empresaId);
    boolean existsByCandidatoIdAndVagaId(Long candidatoId, Long vagaId);
    boolean existsByCandidatoIdAndVagaIdAndStatus(Long candidatoId, Long vagaId, StatusCandidatura status);
}
