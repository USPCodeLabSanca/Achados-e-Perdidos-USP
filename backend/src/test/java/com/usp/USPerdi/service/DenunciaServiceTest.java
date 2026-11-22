package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.DenunciaAtualizarDTO;
import com.usp.USPerdi.dto.DenunciaCriarNovaDTO;
import com.usp.USPerdi.model.Denuncia;
import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.StatusDenuncia;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.DenunciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DenunciaServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private DenunciaService denunciaService;

    private Denuncia mockDenuncia;
    private DenunciaCriarNovaDTO mockNovaDenuncia;
    private Usuario usrDenunciado;
    private Usuario usrDenunciante;
    private Item item;

    @BeforeEach
    void setup() {
        usrDenunciado = new Usuario();
        usrDenunciado.setId(1L);
        usrDenunciante = new Usuario();
        usrDenunciante.setId(2L);
        usrDenunciante.setEmailUsp("email@usp.br");
        item = new Item();
        item.setIdItem(5L);

        mockDenuncia = new Denuncia(
                10L,
                "Fizeram coisa feia",
                Instant.now(),
                usrDenunciado,
                usrDenunciante,
                StatusDenuncia.pendente,
                mock(Item.class)
        );

        mockNovaDenuncia = new DenunciaCriarNovaDTO(
                "Fizeram coisa feia",
                usrDenunciado.getId(),
                item.getIdItem()
        );


    }

    @Test
    void deveCriarNovaDenuncia() {
        when(usuarioService.buscarPorId(usrDenunciado.getId()))
                .thenReturn(usrDenunciado);
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);
        when(denunciaRepository.existsByUsrDenuncianteAndUsrDenunciadoAndStatus(usrDenunciante, usrDenunciado, StatusDenuncia.pendente))
                .thenReturn(false);
        when(itemService.buscarPorId(item.getIdItem()))
                .thenReturn(item);

        denunciaService.criarNovaDenuncia(mockNovaDenuncia, usrDenunciante.getEmailUsp());

        ArgumentCaptor<Denuncia> captor = ArgumentCaptor.forClass(Denuncia.class);
        verify(denunciaRepository).save(captor.capture());

        Denuncia salvo = captor.getValue();

        assertEquals(StatusDenuncia.pendente, salvo.getStatus());
        assertEquals(item, salvo.getItemEnvolvido());
        assertEquals(usrDenunciante, salvo.getUsrDenunciante());
        assertEquals(usrDenunciado, salvo.getUsrDenunciado());
        assertEquals(mockNovaDenuncia.descricao(), salvo.getDescricao());
        assertThat(salvo.getDataDenuncia()).isInThePast();
        assertThat(salvo.getDataDenuncia())
                .isStrictlyBetween(Instant.now().minusSeconds(3600),
                        Instant.now().plusSeconds(3600));
    }

    @Test
    void deveFalharCriarNovaDenuncia_AutoDenuncia() {
        when(usuarioService.buscarPorId(usrDenunciante.getId()))
                .thenReturn(usrDenunciante);
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);

        mockNovaDenuncia = new DenunciaCriarNovaDTO(
                mockNovaDenuncia.descricao(),
                usrDenunciante.getId(),
                mockNovaDenuncia.idItemEnvolvido()
        );

        assertThrows(IllegalArgumentException.class,
                () -> denunciaService.criarNovaDenuncia(mockNovaDenuncia, usrDenunciante.getEmailUsp()));

        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void deveFalharCriarNovaDenuncia_DenunciaAtiva() {
        when(usuarioService.buscarPorId(usrDenunciado.getId()))
                .thenReturn(usrDenunciado);
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);
        when(denunciaRepository.existsByUsrDenuncianteAndUsrDenunciadoAndStatus(usrDenunciante, usrDenunciado, StatusDenuncia.pendente))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> denunciaService.criarNovaDenuncia(mockNovaDenuncia, usrDenunciante.getEmailUsp()));

        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void deveAtualizarDenuncia() {

        mockDenuncia.setStatus(StatusDenuncia.pendente);

        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);

        DenunciaAtualizarDTO dto = new DenunciaAtualizarDTO("nova descrição");

        denunciaService.atualizarDenunciaExistente(mockDenuncia.getIdDenuncia(), usrDenunciante.getEmailUsp(), dto);

        assertEquals("nova descrição", mockDenuncia.getDescricao());


    }

    @Test
    void deveAtualizarDenuncia_SemMudar_Blank() {

        mockDenuncia.setStatus(StatusDenuncia.pendente);

        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);

        DenunciaAtualizarDTO dto = new DenunciaAtualizarDTO("");

        denunciaService.atualizarDenunciaExistente(mockDenuncia.getIdDenuncia(), usrDenunciante.getEmailUsp(), dto);

        assertEquals("Fizeram coisa feia", mockDenuncia.getDescricao());


    }

    @Test
    void deveAtualizarDenuncia_SemMudar_Nulo() {

        mockDenuncia.setStatus(StatusDenuncia.pendente);

        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);

        DenunciaAtualizarDTO dto = new DenunciaAtualizarDTO(null);

        denunciaService.atualizarDenunciaExistente(mockDenuncia.getIdDenuncia(), usrDenunciante.getEmailUsp(), dto);

        assertEquals("Fizeram coisa feia", mockDenuncia.getDescricao());


    }

    @Test
    void deveFalharAtualizarDenuncia_DenunciaResolvida_Aceita() {

        mockDenuncia.setStatus(StatusDenuncia.aceita);

        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));

        DenunciaAtualizarDTO dto = new DenunciaAtualizarDTO("nova descrição");

        assertThrows(IllegalArgumentException.class,
                () -> denunciaService.atualizarDenunciaExistente(mockDenuncia.getIdDenuncia(), usrDenunciante.getEmailUsp(), dto));

    }

    @Test
    void deveFalharAtualizarDenuncia_DenunciaResolvida_Rejeitada() {

        mockDenuncia.setStatus(StatusDenuncia.rejeitada);

        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));

        DenunciaAtualizarDTO dto = new DenunciaAtualizarDTO("nova descrição");

        assertThrows(IllegalArgumentException.class,
                () -> denunciaService.atualizarDenunciaExistente(mockDenuncia.getIdDenuncia(), usrDenunciante.getEmailUsp(), dto));

    }

    @Test
    void deveFalharAtualizarDenuncia_UsuarioNaoPermitido() {

        mockDenuncia.setStatus(StatusDenuncia.pendente);

        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciado.getEmailUsp()))
                .thenReturn(usrDenunciado);

        DenunciaAtualizarDTO dto = new DenunciaAtualizarDTO("nova descrição");

        assertThrows(SecurityException.class,
                () -> denunciaService.atualizarDenunciaExistente(mockDenuncia.getIdDenuncia(), usrDenunciado.getEmailUsp(), dto));

    }

    @Test
    void deveDeletarDenuncia() {
        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);
        mockDenuncia.setStatus(StatusDenuncia.pendente);

        denunciaService.deletarDenuncia(usrDenunciante.getEmailUsp(), mockDenuncia.getIdDenuncia());

        verify(denunciaRepository).delete(mockDenuncia);
    }

    @Test
    void deveFalharDeletarDenuncia_JaJulgada_Aceita() {
        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);
        mockDenuncia.setStatus(StatusDenuncia.aceita);

        assertThrows(IllegalArgumentException.class,
                () -> denunciaService.deletarDenuncia(usrDenunciante.getEmailUsp(), mockDenuncia.getIdDenuncia()));

        verify(denunciaRepository, never()).delete(mockDenuncia);
    }

    @Test
    void deveFalharDeletarDenuncia_JaJulgada_Rejeitada() {
        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciante.getEmailUsp()))
                .thenReturn(usrDenunciante);
        mockDenuncia.setStatus(StatusDenuncia.rejeitada);

        assertThrows(IllegalArgumentException.class,
                () -> denunciaService.deletarDenuncia(usrDenunciante.getEmailUsp(), mockDenuncia.getIdDenuncia()));

        verify(denunciaRepository, never()).delete(mockDenuncia);
    }

    @Test
    void deveFalharDeletarDenuncia_UsuarioNaoPermitido() {
        when(denunciaRepository.findById(mockDenuncia.getIdDenuncia()))
                .thenReturn(Optional.of(mockDenuncia));
        when(usuarioService.buscarPorEmailUSP(usrDenunciado.getEmailUsp()))
                .thenReturn(usrDenunciado);
        mockDenuncia.setStatus(StatusDenuncia.rejeitada);

        assertThrows(SecurityException.class,
                () -> denunciaService.deletarDenuncia(usrDenunciado.getEmailUsp(), mockDenuncia.getIdDenuncia()));

        verify(denunciaRepository, never()).delete(mockDenuncia);
    }


}
