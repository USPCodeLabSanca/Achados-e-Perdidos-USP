package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;

import java.time.Instant;

public record UsuarioResponseDTO(
        Long idUsuario,
        String nome,
        String emailUsp,
        String foto,
        String telefone,
        Campus campus,
        StatusConta statusConta,
        Instant dataRegistro
) {}