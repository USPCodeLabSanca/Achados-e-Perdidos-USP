package com.usp.USPerdi.dto;

import com.usp.USPerdi.ValidationTestBase;
import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioCompletarCadastroDTOValidationTest extends ValidationTestBase {

    @Test
    void emailForaDaUspGeraViolacao() {
        var dto = new UsuarioCompletarCadastroDTO(
                "João", "joao@unicamp.br", Campus.sp_butanta, "51 99999-9999");

        assertThat(camposInvalidos(dto)).contains("emailUsp");
    }

    @Test
    void nomeVazioGeraViolacao() {
        var dto = new UsuarioCompletarCadastroDTO(
                "", "joao@usp.br", Campus.sp_butanta, "51 99999-9999");

        assertThat(camposInvalidos(dto)).contains("nome");
    }

    @Test
    void campusNuloGeraViolacao() {
        var dto = new UsuarioCompletarCadastroDTO(
                "João Pedro", "joao@usp.br", null, "51 99999-9999");

        assertThat(camposInvalidos(dto)).contains("campus");
    }

    @Test
    void telefoneVazioPassa_StringVazia() {
        var dto = new UsuarioCompletarCadastroDTO(
                "João Pedro", "joao@usp.br", Campus.lorena, "");

        assertThat(camposInvalidos(dto)).isEmpty();
    }

    @Test
    void telefoneVazioPassa_Null() {
        var dto = new UsuarioCompletarCadastroDTO(
                "João Pedro", "joao@usp.br", Campus.lorena, null);

        assertThat(camposInvalidos(dto)).isEmpty();
    }

    @Test
    void tudoCertoPassa() {
        var dto = new UsuarioCompletarCadastroDTO(
                "João Pedro", "joao@usp.br", Campus.lorena, null);

        assertThat(camposInvalidos(dto)).isEmpty();
    }
}
