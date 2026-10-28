package com.usp.USPerdi.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name="usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="nome", nullable = false, length = 100)
    private String nome;

    @Column(name="email_usp", nullable = false, length = 100, unique = true)
    private String emailUsp;

    @Column(name="telefone", length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "campus")
    private Campus campus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="status_da_conta")
    private StatusConta statusConta;

    @Column(name="foto", columnDefinition = "TEXT")
    private String foto;

    @Column(name="data_cadastro", insertable = false, updatable = false)
    private Instant dataCadastro;

    public Usuario(Long id, String nome, String emailUsp, String telefone, Campus campus, StatusConta statusConta, String foto, Instant dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.emailUsp = emailUsp;
        this.telefone = telefone;
        this.campus = campus;
        this.statusConta = statusConta;
        this.foto = foto;
        this.dataCadastro = dataCadastro;
    }

    public Usuario() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmailUsp() { return emailUsp; }
    public void setEmailUsp(String emailUsp) { this.emailUsp = emailUsp; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Campus getCampus() { return campus; }
    public void setCampus(Campus campus) { this.campus = campus; }
    public StatusConta getStatusConta() { return statusConta; }
    public void setStatusConta(StatusConta statusConta) { this.statusConta = statusConta; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public Instant getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Instant dataCadastro) { this.dataCadastro = dataCadastro; }
}