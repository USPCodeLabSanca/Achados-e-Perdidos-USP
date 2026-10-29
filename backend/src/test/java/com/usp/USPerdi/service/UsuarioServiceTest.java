package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.UsuarioAtualizarPerfilDTO;
import com.usp.USPerdi.dto.UsuarioCompletarCadastroDTO;
import com.usp.USPerdi.dto.UsuarioResponseDTO;
import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    UsuarioService usuarioService;

    @Test
    public void deveCompletarCadastro() {
        // Criar os dados
        UsuarioCompletarCadastroDTO dto = new UsuarioCompletarCadastroDTO(
                "Joao Pedro",
                "joaopedro@usp.br",
                Campus.sp_butanta,
                "51 99999-9999"
        );
        String fotoGoogle = "url";

        // Mock behavior
        when(usuarioRepository.findByEmailUsp(dto.emailUsp()))
                .thenReturn(Optional.empty());

        // Açao
        usuarioService.completarCadastro(dto, fotoGoogle);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario salvo = captor.getValue();
        assertEquals("Joao Pedro", salvo.getNome());
        assertEquals("joaopedro@usp.br", salvo.getEmailUsp());
        assertEquals(Campus.sp_butanta, salvo.getCampus());
        assertEquals("51 99999-9999", salvo.getTelefone());
        assertEquals(fotoGoogle, salvo.getFoto());
    }

    @Test
    public void deveFalharCadastro_UsuarioJaExiste() {
        // Criar os dados
        UsuarioCompletarCadastroDTO dto = new UsuarioCompletarCadastroDTO(
                "Joao Pedro",
                "joaopedro@usp.br",
                Campus.sp_butanta,
                "51 99999-9999"
        );
        String fotoGoogle = "url";

        Usuario existente = new Usuario();
        existente.setEmailUsp(dto.emailUsp());

        // Mock behavior
        when(usuarioRepository.findByEmailUsp(dto.emailUsp()))
                .thenReturn(Optional.of(existente));

        // Açao
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.completarCadastro(dto, fotoGoogle));

        // Assert
        verify(usuarioRepository, never()).save(any());
    }

    // O teste deveFalharDTOFalho
    // Nao está nessa bateria de testes, pois
    // depende do contexto do spring que irá validar o DTO
    // O teste é redundante com a classe UsuarioCompletarCadastroDTOTest

    @Test
    public void deveBuscarEmailUSP() {
        // Criar os dados
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.ativa,
                "",
                Instant.now()
        );

        // Mock behavior
        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        // Açao
        Usuario retorno = usuarioService.buscarPorEmailUSP(usuario.getEmailUsp());

        // Assert

        verify(usuarioRepository).findByEmailUsp(usuario.getEmailUsp());

        assertEquals(usuario, retorno);
    }

    @Test
    public void deveFalharBuscarEmailUSP() {
        String email = "joaopedro@usp.br";

        // Mock behavior
        when(usuarioRepository.findByEmailUsp(email))
                .thenReturn(Optional.empty());

        // Açao
        // Assert

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.buscarPorEmailUSP(email));

        verify(usuarioRepository).findByEmailUsp(email);
    }

    @Test
    public void deveBuscarNome_SucessoPaginaComUsuario() {
        Pageable pageable = (Pageable) PageRequest.of(0, 10);
        Usuario usuario = new Usuario();
        usuario.setNome("João Pedro");
        Page<Usuario> pagina = new PageImpl<Usuario>(List.of(usuario), pageable, 1);

        when(usuarioRepository.findByNomeContainingIgnoreCase("joao", pageable))
                .thenReturn(pagina);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscaPorNome("joao", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("João Pedro");
        verify(usuarioRepository).findByNomeContainingIgnoreCase("joao", pageable);
    }

    @Test
    public void deveBuscarNome_FalhaPaginaVazia() {
        Pageable pageable = (Pageable) PageRequest.of(0, 10);

        when(usuarioRepository.findByNomeContainingIgnoreCase("joao", pageable))
                .thenReturn(Page.empty(pageable));

        Page<UsuarioResponseDTO> resultado = usuarioService.buscaPorNome("joao", pageable);

        assertThat(resultado.getContent()).hasSize(0);
        assertThat(resultado.getContent().isEmpty()).isTrue();

        verify(usuarioRepository).findByNomeContainingIgnoreCase("joao", pageable);
    }

    @Test
    public void deveBuscarPorStatus_SucessoComUsuario() {
        Pageable pageable = (Pageable) PageRequest.of(0, 10);
        Usuario usuario = new Usuario();
        usuario.setNome("João Pedro");
        usuario.setStatusConta(StatusConta.ativa);
        Page<Usuario> pagina = new PageImpl<Usuario>(List.of(usuario), pageable, 1);

        when(usuarioRepository.findByStatusConta(StatusConta.ativa, pageable))
                .thenReturn(pagina);

        Page<UsuarioResponseDTO> resultado = usuarioService.buscaPorStatusDaConta(StatusConta.ativa, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("João Pedro");
        verify(usuarioRepository).findByStatusConta(StatusConta.ativa, pageable);
    }

    @Test
    public void deveAtualizarUsuario_SucessoInformacoesValidas() {
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.ativa,
                "",
                Instant.now()
        );

        UsuarioAtualizarPerfilDTO dto = new UsuarioAtualizarPerfilDTO(
                "João Pedro dos Santos",
                Campus.sp_butanta,
                "42 111111111"
        );

        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        usuarioService.atualizarPerfil(usuario.getEmailUsp(), dto);

        assertEquals(dto.nome(), usuario.getNome());
        assertEquals(dto.campus(), usuario.getCampus());
        assertEquals(dto.telefone(), usuario.getTelefone());

    }

    @Test
    public void deveFalharAtualizarUsuario_NomeVazio() {
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.ativa,
                "",
                Instant.now()
        );

        UsuarioAtualizarPerfilDTO dto = new UsuarioAtualizarPerfilDTO(
                "",
                null,
                null
        );

        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        usuarioService.atualizarPerfil(usuario.getEmailUsp(), dto);

        assertEquals("João Pedro", usuario.getNome());
    }

    @Test
    public void deveAtualizarUsuario_ApagarTelefone() {
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.ativa,
                "",
                Instant.now()
        );

        UsuarioAtualizarPerfilDTO dto = new UsuarioAtualizarPerfilDTO(
                null,
                null,
                ""
        );

        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        usuarioService.atualizarPerfil(usuario.getEmailUsp(), dto);

        assertNull(usuario.getTelefone());
    }

    @Test
    public void deveFalharAtualizarUsuario_TudoNulo() {
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.ativa,
                "",
                Instant.now()
        );

        UsuarioAtualizarPerfilDTO dto = new UsuarioAtualizarPerfilDTO(
                null,
                null,
                null
        );

        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        usuarioService.atualizarPerfil(usuario.getEmailUsp(), dto);

        assertEquals("João Pedro", usuario.getNome());
        assertEquals("519999999", usuario.getTelefone());
        assertEquals(Campus.lorena, usuario.getCampus());
    }

    @Test
    public void deveDesativarConta() {
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.ativa,
                "",
                Instant.now()
        );

        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        usuarioService.desativarConta(usuario.getEmailUsp());

        assertEquals(StatusConta.inativa, usuario.getStatusConta());
    }

    @Test
    public void deveFalharDesativarConta_UsuarioNaoExiste() {
        String email = "joaopedro@usp.br";

        when(usuarioRepository.findByEmailUsp(email))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.desativarConta(email));
    }

    // Esse aqui é um teste de idempotencia só, não deve mudar status de inativo
    @Test
    public void deveFalharDesativarConta_UsuarioInativo() {
        Usuario usuario = new Usuario(
                0L,
                "João Pedro",
                "joaopedro@usp.br",
                "519999999",
                Campus.lorena,
                StatusConta.inativa,
                "",
                Instant.now()
        );

        when(usuarioRepository.findByEmailUsp(usuario.getEmailUsp()))
                .thenReturn(Optional.of(usuario));

        usuarioService.desativarConta(usuario.getEmailUsp());

        assertEquals(StatusConta.inativa, usuario.getStatusConta());
    }
}