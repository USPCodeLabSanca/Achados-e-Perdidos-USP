package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.StatusItem;

import java.time.LocalDate;

// Todos os campos são opcionais. O frontend envia apenas o que o usuário modificou.
public record ItemExistenteAtualizarDTO(
        String nome,
        String local,
        StatusItem status,
        String descricao,
        String tipo,
        LocalDate dataOcorrencia
) {}
