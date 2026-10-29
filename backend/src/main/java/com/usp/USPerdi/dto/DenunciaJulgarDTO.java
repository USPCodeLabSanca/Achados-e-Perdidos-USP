package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.StatusDenuncia;
import jakarta.validation.constraints.NotNull;

public record DenunciaJulgarDTO(

        @NotNull(message = "O novo status é obrigatório para o julgamento.")
        StatusDenuncia novoStatus
){}
