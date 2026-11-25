package com.usp.USPerdi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

//Serve para padronizar a resposta de erro da API, incluindo informações como timestamp, status HTTP, mensagem de erro e caminho da requisição.

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponseDTO(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> errosCampos
) {
    public ErroResponseDTO(int status, String erro, String mensagem, String caminho) {
        this(Instant.now(), status, erro, mensagem, caminho, null);
    }

    public ErroResponseDTO(int status, String erro, String mensagem, String caminho, Map<String, String> errosCampos) {
        this(Instant.now(), status, erro, mensagem, caminho, errosCampos);
    }
}