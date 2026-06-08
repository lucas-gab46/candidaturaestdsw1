package br.com.lucas.candidaturaestagio.repository;

import br.com.lucas.candidaturaestagio.model.ModalidadeTrabalho;
import br.com.lucas.candidaturaestagio.model.StatusVaga;
import br.com.lucas.candidaturaestagio.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByEmpresaId(Long empresaId);
    List<Vaga> findByAreaAtuacaoIgnoreCase(String areaAtuacao);
    List<Vaga> findByCidadeIgnoreCase(String cidade);
    List<Vaga> findByModalidade(ModalidadeTrabalho modalidade);
    List<Vaga> findByStatus(StatusVaga status);
}
