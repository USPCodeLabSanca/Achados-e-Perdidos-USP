package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.Usuario;

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
) {

    public static UsuarioResponseDTO fromUsuario(Usuario usr) {
        return new UsuarioResponseDTO(
                usr.getId(),
                usr.getNome(),
                usr.getEmailUsp(),
                usr.getFoto(),
                usr.getTelefone(),
                usr.getCampus(),
                usr.getStatusConta(),
                usr.getDataCadastro()
        );
    }

}