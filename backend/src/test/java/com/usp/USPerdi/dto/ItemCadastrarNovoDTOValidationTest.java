package com.usp.USPerdi.dto;

import com.usp.USPerdi.ValidationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemCadastrarNovoDTOValidationTest extends ValidationTestBase {

    @Test
    void nomeVazioGeraViolacao() {
        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "",
                "redondo",
                "",
                "tipo",
                LocalDate.now()
        );

        assertThat(camposInvalidos(dto)).contains("nome");
    }

    @Test
    void nomeEnormeGeraViolacao() {
        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "redondo",
                "",
                "tipo",
                LocalDate.now()
        );

        assertThat(camposInvalidos(dto)).contains("nome");
    }

    @Test
    void localEnormeGeraViolacao() {
        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "caneta",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "",
                "tipo",
                LocalDate.now()
        );

        assertThat(camposInvalidos(dto)).contains("local");
    }

    @Test
    void tipoVazioGeraViolacao() {
        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "caneta",
                "redondo",
                "",
                "",
                LocalDate.now()
        );

        assertThat(camposInvalidos(dto)).contains("tipo");
    }

    @Test
    void tipoEnormeGeraViolacao() {
        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "caneta",
                "redondo",
                "",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                LocalDate.now()
        );

        assertThat(camposInvalidos(dto)).contains("tipo");
    }

    @Test
    void FuturoGeraViolacao() {
        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "caneta",
                "redondo",
                "",
                "canetas",
                LocalDate.MAX
        );

        assertThat(camposInvalidos(dto)).contains("dataOcorrencia");
    }

}
