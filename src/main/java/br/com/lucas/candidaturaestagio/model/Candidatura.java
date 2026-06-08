package br.com.lucas.candidaturaestagio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Candidato candidato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @Lob
    @NotBlank
    private String mensagemApresentacao;

    @Lob
    private String curriculo;

    @Column(nullable = false)
    private LocalDate dataCandidatura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCandidatura status = StatusCandidatura.EM_ANALISE;

    @Lob
    private String justificativaRecusa;

    private LocalDate dataEntrevista;

    private LocalTime horarioEntrevista;

    private String linkVideoconferencia;

    public Candidatura() {
    }

    public Candidatura(Candidato candidato, Vaga vaga, String mensagemApresentacao, String curriculo,
                       StatusCandidatura status) {
        this.candidato = candidato;
        this.vaga = vaga;
        this.mensagemApresentacao = mensagemApresentacao;
        this.curriculo = curriculo;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        if (dataCandidatura == null) {
            dataCandidatura = LocalDate.now();
        }
        if (status == null) {
            status = StatusCandidatura.EM_ANALISE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public String getMensagemApresentacao() {
        return mensagemApresentacao;
    }

    public void setMensagemApresentacao(String mensagemApresentacao) {
        this.mensagemApresentacao = mensagemApresentacao;
    }

    public String getCurriculo() {
        return curriculo;
    }

    public void setCurriculo(String curriculo) {
        this.curriculo = curriculo;
    }

    public LocalDate getDataCandidatura() {
        return dataCandidatura;
    }

    public void setDataCandidatura(LocalDate dataCandidatura) {
        this.dataCandidatura = dataCandidatura;
    }

    public StatusCandidatura getStatus() {
        return status;
    }

    public void setStatus(StatusCandidatura status) {
        this.status = status;
    }

    public String getJustificativaRecusa() {
        return justificativaRecusa;
    }

    public void setJustificativaRecusa(String justificativaRecusa) {
        this.justificativaRecusa = justificativaRecusa;
    }

    public LocalDate getDataEntrevista() {
        return dataEntrevista;
    }

    public void setDataEntrevista(LocalDate dataEntrevista) {
        this.dataEntrevista = dataEntrevista;
    }

    public LocalTime getHorarioEntrevista() {
        return horarioEntrevista;
    }

    public void setHorarioEntrevista(LocalTime horarioEntrevista) {
        this.horarioEntrevista = horarioEntrevista;
    }

    public String getLinkVideoconferencia() {
        return linkVideoconferencia;
    }

    public void setLinkVideoconferencia(String linkVideoconferencia) {
        this.linkVideoconferencia = linkVideoconferencia;
    }
}
