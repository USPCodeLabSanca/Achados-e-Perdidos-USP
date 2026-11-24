package com.usp.USPerdi.repository;

import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.StatusItem;
import com.usp.USPerdi.model.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryPersistenceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void devePersistirItemComStatusCategoriaFotoEUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmailUsp("item-persistencia-" + UUID.randomUUID() + "@usp.br");
        usuario.setCampus(Campus.sp_butanta);
        usuario.setStatusConta(StatusConta.ativa);
        usuario.setFoto("https://google.test/foto-perfil.png");
        usuario = usuarioRepository.saveAndFlush(usuario);

        Item item = new Item();
        item.setNome("Chave encontrada");
        item.setTipo("Chave");
        item.setStatus(StatusItem.encontrado);
        item.setUsuario(usuario);
        item.setFoto(usuario.getFoto());
        item = itemRepository.saveAndFlush(item);

        Long idSalvo = item.getIdItem();
        entityManager.clear();

        Item itemPersistido = itemRepository.findById(idSalvo).orElseThrow();

        assertThat(itemPersistido.getNome()).isEqualTo("Chave encontrada");
        assertThat(itemPersistido.getTipo()).isEqualTo("Chave");
        assertThat(itemPersistido.getStatus()).isEqualTo(StatusItem.encontrado);
        assertThat(itemPersistido.getFoto()).isEqualTo("https://google.test/foto-perfil.png");
        assertThat(itemPersistido.getUsuario().getCampus()).isEqualTo(Campus.sp_butanta);
        assertThat(itemPersistido.getUsuario().getEmailUsp()).isEqualTo(usuario.getEmailUsp());
    }
}
