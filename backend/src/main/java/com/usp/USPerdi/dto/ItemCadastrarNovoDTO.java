package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.StatusItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ItemCadastrarNovoDTO(

        @NotBlank(message = "O campo 'nome' não pode ser vazio")
        @Size(max = 100, message = "O nome do item deve ter no máximo 100 caracteres")
        String nome,

        @Size(max = 150, message = "O nome de 'local' deve ter no máximo 150 caracteres")
        String local,

        String descricao,

        @NotBlank(message = "O nome de 'tipo' não pode ser vazio")
        @Size(max= 50, message = "O nome de 'tipo' deve ter no máximo 50 caracteres")
        String tipo,

        @PastOrPresent(message = "A data da ocorrência não pode estar no futuro")
        LocalDate dataOcorrencia
) {}
