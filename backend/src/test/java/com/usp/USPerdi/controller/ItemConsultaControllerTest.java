package com.usp.USPerdi.controller;

import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.StatusItem;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.ItemRepository;
import com.usp.USPerdi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemConsultaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtEncoder jwtEncoder;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private EntityManager entityManager;

    private Usuario autor;
    private String token;

    @BeforeEach
    void prepararDados() {
        String email = "consulta-" + UUID.randomUUID() + "@usp.br";
        autor = new Usuario();
        autor.setNome("Autor Teste");
        autor.setEmailUsp(email);
        autor.setTelefone("16999999999");
        autor.setCampus(Campus.sao_carlos);
        autor.setStatusConta(StatusConta.ativa);
        autor.setFoto("https://google.test/autor.png");
        autor = usuarioRepository.saveAndFlush(autor);

        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("usperdi")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(300))
                .subject(email)
                .claim("email", email)
                .build();
        token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Test
void deveListarAchadosEPerdidosAtivosOrdenadosPorData() throws Exception {
    Item perdidoAntigo = salvarItem("Carteira Preta", StatusItem.perdido, "http://localhost:8080/uploads/carteira.png");
    Item achadoRecente = salvarItem("Garrafa Azul", StatusItem.encontrado, "http://localhost:8080/uploads/garrafa.png");
    Item recuperado = salvarItem("Chave Recuperada", StatusItem.recuperado, "http://localhost:8080/uploads/chave.png");

    entityManager.createNativeQuery("UPDATE item SET data_registro = NOW() - INTERVAL '1 hour' WHERE id = :id")
            .setParameter("id", perdidoAntigo.getIdItem())
            .executeUpdate();
    entityManager.clear();

    mockMvc.perform(get("/v1/items")
                    .param("size", "100")
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].idItem", hasItem(achadoRecente.getIdItem().intValue())))
            .andExpect(jsonPath("$.content[*].idItem", hasItem(perdidoAntigo.getIdItem().intValue())))
            .andExpect(jsonPath("$.content[*].idItem", not(hasItem(recuperado.getIdItem().intValue()))))
            .andExpect(jsonPath("$.content[0].idItem").value(achadoRecente.getIdItem()))
            .andExpect(jsonPath("$.content[0].foto").value("http://localhost:8080/uploads/garrafa.png"))
            .andExpect(jsonPath("$.content[0].criadorNome").value("Autor Teste"))
            .andExpect(jsonPath("$.content[0].criadorEmail").value(autor.getEmailUsp()));
}

    @Test
    void deveRetornarDetalhesDoItemComAutorEImagem() throws Exception {
        Item item = salvarItem("Caderno Espiral", StatusItem.encontrado, "http://localhost:8080/uploads/caderno.png");

        mockMvc.perform(get("/v1/items/{id}", item.getIdItem())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idItem").value(item.getIdItem()))
                .andExpect(jsonPath("$.nome").value("Caderno Espiral"))
                .andExpect(jsonPath("$.status").value("encontrado"))
                .andExpect(jsonPath("$.foto").value("http://localhost:8080/uploads/caderno.png"))
                .andExpect(jsonPath("$.criadorNome").value("Autor Teste"))
                .andExpect(jsonPath("$.criadorEmail").value(autor.getEmailUsp()))
                .andExpect(jsonPath("$.criadorTelefone").value("16999999999"))
                .andExpect(jsonPath("$.criadorFoto").value("https://google.test/autor.png"));
    }

    @Test
    void deveRetornarQuatrocentosEQuatroQuandoItemNaoExiste() throws Exception {
        mockMvc.perform(get("/v1/items/{id}", 999999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private Item salvarItem(String nome, StatusItem status, String foto) {
        Item item = new Item();
        item.setNome(nome);
        item.setLocal("ICMC");
        item.setDescricao("Descricao de teste");
        item.setTipo("Objeto");
        item.setStatus(status);
        item.setDataOcorrencia(LocalDate.now());
        item.setFoto(foto);
        item.setUsuario(autor);
        return itemRepository.saveAndFlush(item);
    }
}