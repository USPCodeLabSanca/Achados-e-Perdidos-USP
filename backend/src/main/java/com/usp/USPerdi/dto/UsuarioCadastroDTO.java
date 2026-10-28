package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Campus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// Tratará quais dados aceitar do frontend e enviar para o backend (services)
public record UsuarioCadastroDTO(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail USP é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@([A-Za-z0-9-]+\\.)*usp\\.br$", message = "Apenas e-mails institucionais da USP são permitidos")
        String emailUsp,

        @NotNull(message = "O campus é obrigatório")
        Campus campus,

        // Telefone e Foto são opcionais, então não levam anotações de validação
        String telefone,

        String foto

        ){}
