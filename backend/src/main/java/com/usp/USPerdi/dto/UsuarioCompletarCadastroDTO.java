package com.usp.USPerdi.dto;

import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.Usuario;
import jakarta.validation.constraints.*;

// Tratará quais dados aceitar do frontend e enviar para o backend (services)
public record UsuarioCompletarCadastroDTO(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        @Size(max = 100, message = "O tamanho máximo de 'nome' é de 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail USP é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@([A-Za-z0-9-]+\\.)*usp\\.br$", message = "Apenas e-mails institucionais da USP são permitidos")
        @Size(max = 100, message = "O tamanho máximo de email é de 100 caracteres")
        String emailUsp,

        @NotNull(message = "O campus é obrigatório")
        Campus campus,

        // Telefone é opcional, então não leva anotação de validação
        @Size(max=20, message = "O tamanho maximo de telefone é 20 caracteres")
        String telefone

        // Sobre a foto: o Google, na criacao, ja envia uma foto por padrao. Entao, na criacao nao é preciso receber no DTO

        ) {

        public Usuario toUsuario() {
                Usuario usr = new Usuario();
                usr.setNome(nome());
                usr.setCampus(campus());
                usr.setEmailUsp(emailUsp());
                usr.setTelefone(telefone());
                usr.setStatusConta(StatusConta.ativa);

                return usr;
        }

}
