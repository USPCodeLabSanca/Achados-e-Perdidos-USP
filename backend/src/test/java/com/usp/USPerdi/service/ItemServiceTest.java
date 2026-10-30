package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.ItemCadastrarNovoDTO;
import com.usp.USPerdi.dto.ItemResponseDTO;
import com.usp.USPerdi.model.*;
import com.usp.USPerdi.repository.ItemRepository;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ItemService itemService;

    @Test
    void deveBuscarPorId() {
        Item item = new Item();
        item.setIdItem(10L);

        when(itemRepository.findById(10L))
                .thenReturn(Optional.of(item));

        Item buscado = itemService.buscarPorId(10L);

        Assertions.assertEquals(item, buscado);
    }

    @Test
    void deveFalharBuscaPorId_IdInexistente() {
        when(itemRepository.findById(10L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemService.buscarPorId(10L));
    }

    @Test
    void deveCadastrarNovoItem() {
        String emailUsp = "email@usp.br";
        Usuario usuario = new Usuario();
        usuario.setEmailUsp(emailUsp);
        MultipartFile mockFile = mock(MultipartFile.class);

        ItemCadastrarNovoDTO dto = new ItemCadastrarNovoDTO(
                "Carrinho Hot wheels",
                "Redondo",
                "Perderam um carrinho no redondo",
                "Brinquedo",
                LocalDate.now()
        );

        when(mockFile.isEmpty())
                .thenReturn(false);

        when(usuarioService.buscarPorEmailUSP(emailUsp))
                .thenReturn(usuario);

        when(fileStorageService.salvarArquivo(mockFile))
                .thenReturn("uploads/arquivo");

        itemService.cadastrarNovoItem(emailUsp, dto, mockFile, StatusItem.perdido);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item salvo = captor.getValue();

        Assertions.assertEquals(usuario, salvo.getUsuario());
        Assertions.assertEquals(StatusItem.perdido, salvo.getStatus());
        Assertions.assertEquals("uploads/arquivo", salvo.getFoto());
        Assertions.assertEquals(dto.nome(), salvo.getNome());
        Assertions.assertEquals(dto.local(), salvo.getLocal());
        Assertions.assertEquals(dto.descricao(), salvo.getDescricao());
        Assertions.assertEquals(dto.tipo(), salvo.getTipo());
        Assertions.assertEquals(dto.dataOcorrencia(), salvo.getDataOcorrencia());
    }

    @Test
    void deveFalharCadastrarNovoItem_UsuarioInexistente() {
        String emailUsp = "email@usp.br";

        when(usuarioService.buscarPorEmailUSP(emailUsp))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemService.cadastrarNovoItem(emailUsp, mock(ItemCadastrarNovoDTO.class), mock(MultipartFile.class), StatusItem.perdido));

    }

    @Test
    void deveFalharCadastrarNovoItem_SemImagem_ConteudoVazio() {
        String emailUsp = "email@usp.br";
        Usuario usuario = new Usuario();
        usuario.setEmailUsp(emailUsp);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty())
                .thenReturn(true);

        when(usuarioService.buscarPorEmailUSP(emailUsp))
                .thenReturn(usuario);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemService.cadastrarNovoItem(emailUsp, mock(ItemCadastrarNovoDTO.class), mockFile, StatusItem.perdido));
    }

    @Test
    void deveFalharCadastrarNovoItem_SemImagem_Nulo() {
        String emailUsp = "email@usp.br";
        Usuario usuario = new Usuario();
        usuario.setEmailUsp(emailUsp);
        MultipartFile mockFile = null;

        when(usuarioService.buscarPorEmailUSP(emailUsp))
                .thenReturn(usuario);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> itemService.cadastrarNovoItem(emailUsp, mock(ItemCadastrarNovoDTO.class), mockFile, StatusItem.perdido));
    }

    @Test
    public void deveBuscarTodosPorNome() {
        // Criar os dados
        String nome = "carteira";
        Pageable pageable = PageRequest.of(0, 10);

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

        Item item1 = new Item();
        item1.setNome("Carteira preta");
        item1.setLocal("Biblioteca");
        item1.setStatus(StatusItem.perdido);
        item1.setUsuario(usuario);
        item1.setDataOcorrencia(LocalDate.now());

        Item item2 = new Item();
        item2.setNome("Carteira marrom");
        item2.setLocal("Bandejão");
        item2.setStatus(StatusItem.perdido);
        item2.setUsuario(usuario);
        item2.setDataOcorrencia(LocalDate.now());

        Page<Item> pageItens = new PageImpl<>(List.of(item1, item2), pageable, 2);

        // Mock behavior
        when(itemRepository.findByNomeContainingIgnoreCase(nome, pageable))
                .thenReturn(pageItens);

        // Açao
        Page<ItemResponseDTO> retorno = itemService.buscaTodosPorNome(nome, pageable);

        // Assert
        verify(itemRepository).findByNomeContainingIgnoreCase(nome, pageable);

        assertEquals(2, retorno.getTotalElements());
        assertEquals(2, retorno.getContent().size());
        assertEquals(ItemResponseDTO.fromItem(item1), retorno.getContent().get(0));
        assertEquals(ItemResponseDTO.fromItem(item2), retorno.getContent().get(1));
    }

    @Test
    public void deveRetornarPaginaVaziaBuscarTodosPorNome() {
        String nome = "inexistente";
        Pageable pageable = PageRequest.of(0, 10);

        // Mock behavior
        when(itemRepository.findByNomeContainingIgnoreCase(nome, pageable))
                .thenReturn(Page.empty(pageable));

        // Açao
        Page<ItemResponseDTO> retorno = itemService.buscaTodosPorNome(nome, pageable);

        // Assert
        verify(itemRepository).findByNomeContainingIgnoreCase(nome, pageable);

        assertTrue(retorno.isEmpty());
        assertEquals(0, retorno.getTotalElements());
    }

    @Test
    public void deveBuscarTodosPorUsuario() {
        // Criar os dados
        Pageable pageable = PageRequest.of(0, 10);

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

        Item item1 = new Item();
        item1.setNome("Carteira");
        item1.setLocal("Biblioteca");
        item1.setStatus(StatusItem.perdido);
        item1.setUsuario(usuario);
        item1.setDataOcorrencia(LocalDate.now());

        Item item2 = new Item();
        item2.setNome("Chave");
        item2.setLocal("Bandejão");
        item2.setStatus(StatusItem.perdido);
        item2.setUsuario(usuario);
        item2.setDataOcorrencia(LocalDate.now());

        Page<Item> pageItens = new PageImpl<>(List.of(item1, item2), pageable, 2);

        // Mock behavior
        when(usuarioService.buscarPorEmailUSP(usuario.getEmailUsp()))
                .thenReturn(usuario);
        when(itemRepository.findByUsuario(usuario, pageable))
                .thenReturn(pageItens);

        // Açao
        Page<ItemResponseDTO> retorno =
                itemService.buscaTodosPorUsuario(usuario.getEmailUsp(), pageable);

        // Assert
        verify(usuarioService).buscarPorEmailUSP(usuario.getEmailUsp());
        verify(itemRepository).findByUsuario(usuario, pageable);

        assertEquals(2, retorno.getTotalElements());
        assertEquals(ItemResponseDTO.fromItem(item1), retorno.getContent().get(0));
        assertEquals(ItemResponseDTO.fromItem(item2), retorno.getContent().get(1));
    }

    @Test
    public void deveFalharBuscarTodosPorUsuario_UsuarioNaoExiste() {
        String email = "inexistente@usp.br";
        Pageable pageable = PageRequest.of(0, 10);

        // Mock behavior
        when(usuarioService.buscarPorEmailUSP(email))
                .thenThrow(new IllegalArgumentException());

        // Açao
        // Assert
        assertThrows(IllegalArgumentException.class,
                () -> itemService.buscaTodosPorUsuario(email, pageable));

        verify(usuarioService).buscarPorEmailUSP(email);
        verify(itemRepository, never()).findByUsuario(any(), any());
    }

    @Test
    public void deveBuscarMaisRecentesPorStatus() {
        // Criar os dados
        StatusItem status = StatusItem.perdido;
        Pageable pageable = PageRequest.of(0, 10);

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

        Item maisRecente = new Item();
        maisRecente.setNome("Carteira");
        maisRecente.setLocal("Biblioteca");
        maisRecente.setStatus(status);
        maisRecente.setUsuario(usuario);
        maisRecente.setDataOcorrencia(LocalDate.now());

        Item maisAntigo = new Item();
        maisAntigo.setNome("Chave");
        maisAntigo.setLocal("Bandejão");
        maisAntigo.setStatus(status);
        maisAntigo.setUsuario(usuario);
        maisAntigo.setDataOcorrencia(LocalDate.now().minusDays(1L));

        Page<Item> pageItens = new PageImpl<>(List.of(maisRecente, maisAntigo), pageable, 2);

        // Mock behavior
        when(itemRepository.findByStatusOrderByDataOcorrenciaDesc(status, pageable))
                .thenReturn(pageItens);

        // Açao
        Page<ItemResponseDTO> retorno =
                itemService.buscaMaisRecentesPorStatus(status, pageable);

        // Assert
        verify(itemRepository).findByStatusOrderByDataOcorrenciaDesc(status, pageable);

        assertEquals(2, retorno.getTotalElements());
        assertEquals(ItemResponseDTO.fromItem(maisRecente), retorno.getContent().get(0));
        assertEquals(ItemResponseDTO.fromItem(maisAntigo), retorno.getContent().get(1));
    }

    @Test
    public void deveRetornarPaginaVaziaBuscarMaisRecentesPorStatus() {
        StatusItem status = StatusItem.encontrado;
        Pageable pageable = PageRequest.of(0, 10);

        // Mock behavior
        when(itemRepository.findByStatusOrderByDataOcorrenciaDesc(status, pageable))
                .thenReturn(Page.empty(pageable));

        // Açao
        Page<ItemResponseDTO> retorno =
                itemService.buscaMaisRecentesPorStatus(status, pageable);

        // Assert
        verify(itemRepository).findByStatusOrderByDataOcorrenciaDesc(status, pageable);

        assertTrue(retorno.isEmpty());
    }

    @Test
    public void deveBuscarPorLocal() {
        // Criar os dados
        String local = "biblioteca";
        Pageable pageable = PageRequest.of(0, 10);

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

        Item item1 = new Item();
        item1.setNome("Carteira");
        item1.setLocal("Biblioteca Central");
        item1.setStatus(StatusItem.perdido);
        item1.setUsuario(usuario);
        item1.setDataOcorrencia(LocalDate.now());

        Item item2 = new Item();
        item2.setNome("Chave");
        item2.setLocal("Biblioteca Setorial");
        item2.setStatus(StatusItem.encontrado);
        item2.setUsuario(usuario);
        item2.setDataOcorrencia(LocalDate.now());

        Page<Item> pageItens = new PageImpl<>(List.of(item1, item2), pageable, 2);

        // Mock behavior
        when(itemRepository.findByLocalContainingIgnoreCase(local, pageable))
                .thenReturn(pageItens);

        // Açao
        Page<ItemResponseDTO> retorno = itemService.buscarPorLocal(local, pageable);

        // Assert
        verify(itemRepository).findByLocalContainingIgnoreCase(local, pageable);

        assertEquals(2, retorno.getTotalElements());
        assertEquals(ItemResponseDTO.fromItem(item1), retorno.getContent().get(0));
        assertEquals(ItemResponseDTO.fromItem(item2), retorno.getContent().get(1));
    }

    @Test
    public void deveRetornarPaginaVaziaBuscarPorLocal() {
        String local = "local inexistente";
        Pageable pageable = PageRequest.of(0, 10);

        // Mock behavior
        when(itemRepository.findByLocalContainingIgnoreCase(local, pageable))
                .thenReturn(Page.empty(pageable));

        // Açao
        Page<ItemResponseDTO> retorno = itemService.buscarPorLocal(local, pageable);

        // Assert
        verify(itemRepository).findByLocalContainingIgnoreCase(local, pageable);

        assertTrue(retorno.isEmpty());
        assertEquals(0, retorno.getTotalElements());
    }

}