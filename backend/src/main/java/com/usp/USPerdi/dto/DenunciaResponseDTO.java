package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.StatusDenuncia;

import java.time.Instant;

public record DenunciaResponseDTO(

        Long idDenuncia,
        String descricao,
        Instant dataDenuncia,
        StatusDenuncia statusDenuncia,

        // Dados denunciante
        Long idDenunciante,
        String nomeDenunciante,

        // Dados denunciado
        Long idDenunciado,
        String nomeDenunciado,

        // Dados do item (se tiver)
        Long idItemEnvolvido,
        String nomeItemEnvolvido

) {}
