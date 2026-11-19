package com.usp.USPerdi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="recupera")
public class Recupera {

    @EmbeddedId
    private RecuperaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate data;

    @Column(columnDefinition = "TEXT")
    private String observacao;


    // Constructors
    public Recupera() {}

    public Recupera(RecuperaId id, Item item, Usuario usuario, LocalDate data, String observacao) {
        this.id = id;
        this.item = item;
        this.usuario = usuario;
        this.data = data;
        this.observacao = observacao;
    }


    // Getters and Setters


    public RecuperaId getId() {
        return id;
    }

    public void setId(RecuperaId id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}