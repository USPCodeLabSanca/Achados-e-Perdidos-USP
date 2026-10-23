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
    @Column(name = "id")
    private Long idDenuncia;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(name = "data", nullable = false)
    private Instant dataDenuncia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_denunciado", nullable = false)
    private Usuario usrDenunciado;

    @ManyToOne
    @JoinColumn(name="id_denunciante", nullable = false)
    private Usuario usrDenunciante;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private StatusDenuncia status = StatusDenuncia.pendente;

    // Constructors

    public Denuncia(Long idDenuncia, String descricao, Instant dataDenuncia, Usuario usrDenunciado, Usuario usrDenunciante, StatusDenuncia status) {
        this.idDenuncia = idDenuncia;
        this.descricao = descricao;
        this.dataDenuncia = dataDenuncia;
        this.usrDenunciado = usrDenunciado;
        this.usrDenunciante = usrDenunciante;
        this.status = status;
    }

    public Denuncia() {
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

    public Usuario getUsrDenunciado() {
        return usrDenunciado;
    }

    public void setUsrDenunciado(Usuario usrDenunciado) {
        this.usrDenunciado = usrDenunciado;
    }

    public Usuario getUsrDenunciante() {
        return usrDenunciante;
    }

    public void setUsrDenunciante(Usuario usrDenunciante) {
        this.usrDenunciante = usrDenunciante;
    }

    public StatusDenuncia getStatus() {
        return status;
    }

    public void setStatus(StatusDenuncia status) {
        this.status = status;
    }
}