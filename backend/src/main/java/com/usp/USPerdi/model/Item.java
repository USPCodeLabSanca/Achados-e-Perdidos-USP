package com.usp.USPerdi.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="item")
public class Item {

    // Attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long idItem;

    @Column(name="nome", nullable = false, length = 100)
    private String nome;

    @Column(name="foto", columnDefinition = "TEXT")
    private String foto;

    @Column(name="local", length = 150)
    private String local;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="status", nullable = false)
    private StatusItem status;

    @Column(name="descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name="tipo", length = 50)
    private String tipo;

    @Column(name="data_registro", insertable = false, updatable = false)
    private Instant dataRegistro;

    @Column(name="data_ocorrencia")
    private LocalDate dataOcorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Constructors

    public Item(Long idItem, String nome, String foto, String local, StatusItem status, String descricao, String tipo, Instant dataRegistro, LocalDate dataOcorrencia, Usuario usuario) {
        this.idItem = idItem;
        this.nome = nome;
        this.foto = foto;
        this.local = local;
        this.status = status;
        this.descricao = descricao;
        this.tipo = tipo;
        this.dataRegistro = dataRegistro;
        this.dataOcorrencia = dataOcorrencia;
        this.usuario = usuario;
    }

    public Item() {
    }

    // Getters e Setters


    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public StatusItem getStatus() {
        return status;
    }

    public void setStatus(StatusItem status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Instant getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Instant dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public LocalDate getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(LocalDate dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
