package com.usp.USPerdi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DenunciaCriarNovaDTO(

        @NotBlank(message="A descrição não pode ser vazia!")
        @Size(min = 50, max = 400, message = "O tamanho da descrição deve ser entre 50 e 400 caracteres")
        String descricao,

        @NotNull(message = "O ID do usuário denunciado é obrigatório")
        Long idDenunciado,

        Long idItemEnvolvido // É opcional, para caso a denúncia seja diretamente do usuário de alguém
){}
