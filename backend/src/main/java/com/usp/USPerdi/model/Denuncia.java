package com.usp.USPerdi.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;

@Entity
@Table(name="denuncia")
public class Denuncia {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long idDenuncia;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(name = "data", nullable = false)
    private Instant dataDenuncia;

    @Column(name = "id_denunciado", nullable = false)
    private Long idDenunciado;

    @Column(name = "id_denunciante", nullable = false)
    private Long idDenunciante;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private StatusDenuncia status = StatusDenuncia.pendente;

    // Constructors
    public Denuncia() {
    }

    public Denuncia(Long idDenuncia, String descricao, Instant dataDenuncia, Long idDenunciado, Long idDenunciante, StatusDenuncia status) {
        this.idDenuncia = idDenuncia;
        this.descricao = descricao;
        this.dataDenuncia = dataDenuncia;
        this.idDenunciado = idDenunciado;
        this.idDenunciante = idDenunciante;
        this.status = status;
    }

    // Getters e Setters
    public Long getIdDenuncia() {
        return idDenuncia;
    }

    public void setIdDenuncia(Long idDenuncia) {
        this.idDenuncia = idDenuncia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Instant getDataDenuncia() {
        return dataDenuncia;
    }

    public void setDataDenuncia(Instant dataDenuncia) {
        this.dataDenuncia = dataDenuncia;
    }

    public Long getIdDenunciado() {
        return idDenunciado;
    }

    public void setIdDenunciado(Long idDenunciado) {
        this.idDenunciado = idDenunciado;
    }

    public Long getIdDenunciante() {
        return idDenunciante;
    }

    public void setIdDenunciante(Long idDenunciante) {
        this.idDenunciante = idDenunciante;
    }

    public StatusDenuncia getStatus() {
        return status;
    }

    public void setStatus(StatusDenuncia status) {
        this.status = status;
    }
}
