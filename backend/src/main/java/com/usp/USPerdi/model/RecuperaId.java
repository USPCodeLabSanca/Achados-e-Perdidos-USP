package com.usp.USPerdi.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RecuperaId implements Serializable {

    private Long itemId;
    private Long usuarioId;

    public RecuperaId() {
    }

    public RecuperaId(Long itemId, Long usuarioId) {
        this.itemId = itemId;
        this.usuarioId = usuarioId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecuperaId that = (RecuperaId) o;
        return Objects.equals(itemId, that.itemId) &&
                Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, usuarioId);
    }
}