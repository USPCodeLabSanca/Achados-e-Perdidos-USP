package com.usp.USPerdi.dto;

import jakarta.validation.constraints.Size;

public record DenunciaAtualizarDTO(
        @Size(min = 50, max = 400, message = "O tamanho da descrição deve ser entre 50 e 400 caracteres")
        String descricao
) {}
