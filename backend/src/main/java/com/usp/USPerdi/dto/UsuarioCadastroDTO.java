package com.usp.USPerdi.dto;

import jakarta.validation.constraints.NotBlank;

// Tratará quais dados aceitar do frontend e enviar para o backend (services)
public record UsuarioCadastroDTO(
        @NotBlank

        ){}
