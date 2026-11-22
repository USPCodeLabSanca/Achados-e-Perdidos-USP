package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Denuncia;
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

) {

    public static DenunciaResponseDTO fromDenuncia(Denuncia denuncia) {

        // 1. Se item não existir, fica null
        Long idItem = null;
        String nomeItem = null;

        if (denuncia.getItemEnvolvido() != null) {
            idItem = denuncia.getItemEnvolvido().getIdItem();
            nomeItem = denuncia.getItemEnvolvido().getNome();
        }

        // 2. Monta o DTO
        return new DenunciaResponseDTO(
                denuncia.getIdDenuncia(),
                denuncia.getDescricao(),
                denuncia.getDataDenuncia(),
                denuncia.getStatus(),

                // Dados do Denunciante (Quem abriu a denúncia)
                denuncia.getUsrDenunciante().getId(),
                denuncia.getUsrDenunciante().getNome(),

                // Dados do Denunciado (O acusado)
                denuncia.getUsrDenunciado().getId(),
                denuncia.getUsrDenunciado().getNome(),

                // Dados do Item Opcional
                idItem,
                nomeItem
        );

    }

}
