package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Campus;

// Todos os campos são opcionais. O frontend envia apenas o que o usuário modificou.
public record UsuarioAtualizarPerfilDTO(
        String nome,
        Campus campus,
        String telefone
) {}