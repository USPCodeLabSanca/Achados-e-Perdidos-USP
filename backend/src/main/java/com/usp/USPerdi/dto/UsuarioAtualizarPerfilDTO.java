package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Campus;
import jakarta.validation.constraints.Size;

// Todos os campos são opcionais. O frontend envia apenas o que o usuário modificou.
public record UsuarioAtualizarPerfilDTO(

        @Size(max = 100, message = "O tamanho máximo de 'nome' é de 100 caracteres")
        String nome,
        Campus campus,
        @Size(max=20, message = "O tamanho maximo de telefone é 20 caracteres")
        String telefone
) {}