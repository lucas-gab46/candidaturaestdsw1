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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @Lob
    @NotBlank
    private String descricao;

    @NotBlank
    private String areaAtuacao;

    @Lob
    @NotBlank
    private String requisitos;

    @Min(1)
    private Integer cargaHoraria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeTrabalho modalidade;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal valorBolsa;

    @Lob
    private String beneficios;

    @NotBlank
    private String cidade;

    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVaga status = StatusVaga.ABERTA;

    public Vaga() {
    }

    public Vaga(Empresa empresa, String titulo, String descricao, String areaAtuacao, String requisitos,
                Integer cargaHoraria, ModalidadeTrabalho modalidade, BigDecimal valorBolsa, String beneficios,
                String cidade, LocalDate dataLimite, StatusVaga status) {
        this.empresa = empresa;
        this.titulo = titulo;
        this.descricao = descricao;
        this.areaAtuacao = areaAtuacao;
        this.requisitos = requisitos;
        this.cargaHoraria = cargaHoraria;
        this.modalidade = modalidade;
        this.valorBolsa = valorBolsa;
        this.beneficios = beneficios;
        this.cidade = cidade;
        this.dataLimite = dataLimite;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public ModalidadeTrabalho getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeTrabalho modalidade) {
        this.modalidade = modalidade;
    }

    public BigDecimal getValorBolsa() {
        return valorBolsa;
    }

    public void setValorBolsa(BigDecimal valorBolsa) {
        this.valorBolsa = valorBolsa;
    }

    public String getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(String beneficios) {
        this.beneficios = beneficios;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }
}
