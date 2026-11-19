package com.usp.USPerdi.exception;

import com.usp.USPerdi.controller.UsuarioController;
import com.usp.USPerdi.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
        void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setValidator(validator)
                .build();
        }

    @Test
    @DisplayName("Deve retornar 404 quando RecursoNaoEncontradoException for lançada")
    void deveRetornar404QuandoRecursoNaoEncontrado() throws Exception {
        when(usuarioService.obterPerfilUsuario("inexistente@usp.br"))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado com o e-mail: inexistente@usp.br"));

        mockMvc.perform(get("/v1/usuarios/inexistente@usp.br"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado com o e-mail: inexistente@usp.br"))
                .andExpect(jsonPath("$.caminho").value("/v1/usuarios/inexistente@usp.br"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando ConflitoDeRecursoException for lançada")
    void deveRetornar409QuandoConflitoDeRecurso() throws Exception {
        when(usuarioService.completarCadastro(any(), any()))
                .thenThrow(new ConflitoDeRecursoException("Este e-mail USP já possui cadastro no sistema."));

        String jsonValido = """
                {
                    "nome": "Murilo Nakata",
                    "emailUsp": "murilo@usp.br",
                    "campus": "sao_carlos"
                }
                """;

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflito de dados"))
                .andExpect(jsonPath("$.mensagem").value("Este e-mail USP já possui cadastro no sistema."));
    }

    @Test
    @DisplayName("Deve retornar 403 quando AcessoNegadoException for lançada")
    void deveRetornar403QuandoAcessoNegado() throws Exception {
        when(usuarioService.obterPerfilUsuario("outro@usp.br"))
                .thenThrow(new AcessoNegadoException("Você não tem permissão para editar este item."));

        mockMvc.perform(get("/v1/usuarios/outro@usp.br"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Acesso negado"))
                .andExpect(jsonPath("$.mensagem").value("Você não tem permissão para editar este item."));
    }

    @Test
    @DisplayName("Deve retornar 400 quando RegraDeNegocioException for lançada")
    void deveRetornar400QuandoRegraDeNegocioViolada() throws Exception {
        when(usuarioService.completarCadastro(any(), any()))
                .thenThrow(new RegraDeNegocioException("Não é permitido auto-denúncia."));

        String jsonValido = """
                {
                    "nome": "Murilo Nakata",
                    "emailUsp": "murilo@usp.br",
                    "campus": "sao_carlos"
                }
                """;

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Regra de negócio violada"))
                .andExpect(jsonPath("$.mensagem").value("Não é permitido auto-denúncia."));
    }

    @Test
    @DisplayName("Deve retornar 400 com errosCampos quando validação @Valid falhar")
    void deveRetornar400QuandoValidacaoFalhar() throws Exception {
        String jsonInvalido = """
                {
                    "nome": "",
                    "emailUsp": "aluno@gmail.com",
                    "campus": "sao_carlos"
                }
                """;

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Erro de validação"))
                .andExpect(jsonPath("$.errosCampos.nome").value("O campo 'nome' é obrigatório"))
                .andExpect(jsonPath("$.errosCampos.emailUsp").value("Apenas e-mails institucionais da USP são permitidos"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando Enum ou JSON da requisição for inválido")
    void deveRetornar400QuandoJsonOuEnumInvalido() throws Exception {
        String jsonEnumInvalido = """
                {
                    "nome": "Murilo Nakata",
                    "emailUsp": "murilo@usp.br",
                    "campus": "CAMPUS_INEXISTENTE"
                }
                """;

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEnumInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Requisição inválida"));
    }

    @Test
    @DisplayName("Deve retornar 500 quando ArmazenamentoArquivoException ou erro inesperado ocorrer")
    void deveRetornar500QuandoErroDeArquivoOuInesperado() throws Exception {
        when(usuarioService.obterPerfilUsuario("erro-arquivo@usp.br"))
                .thenThrow(new ArmazenamentoArquivoException("Falha ao armazenar a imagem.", new RuntimeException("IO")));

        mockMvc.perform(get("/v1/usuarios/erro-arquivo@usp.br"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.erro").value("Erro ao processar arquivo"))
                .andExpect(jsonPath("$.mensagem").value("Falha ao armazenar a imagem."));

        when(usuarioService.obterPerfilUsuario("erro-interno@usp.br"))
                .thenThrow(new NullPointerException("Detalhe interno que não deve vazar"));

        mockMvc.perform(get("/v1/usuarios/erro-interno@usp.br"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.erro").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.mensagem").value("Ocorreu um erro inesperado. Tente novamente mais tarde."));
    }
}