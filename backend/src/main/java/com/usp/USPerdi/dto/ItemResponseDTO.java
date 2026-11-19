package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Item;
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
) {

    public static ItemResponseDTO fromItem(Item item) {
        return new ItemResponseDTO(
                item.getIdItem(),
                item.getNome(),
                item.getFoto(),
                item.getLocal(),
                item.getStatus(),
                item.getDescricao(),
                item.getTipo(),
                item.getDataOcorrencia(),

                // Puxamos apenas o que o frontend precisa do usuário
                item.getUsuario().getNome(),
                item.getUsuario().getEmailUsp(),
                item.getUsuario().getTelefone(),
                item.getUsuario().getFoto()
        );
    }

}