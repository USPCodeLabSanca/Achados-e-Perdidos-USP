package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.StatusItem;
import java.time.LocalDate;

public record ItemResponseDTO(
        Long idItem,
        String nome,
        String foto,
        String local,
        StatusItem status,
        String descricao,
        String tipo,
        LocalDate dataOcorrencia,

        // Dados do usuário desmembrados sob medida para o Frontend
        String criadorNome,
        String criadorEmail,     // Identificador único (serve para o chat ou e-mail)
        String criadorTelefone,  // Serve para o botão "Chamar no WhatsApp"
        String criadorFoto       // Para mostrar a fotinha dele no card do item
) {}