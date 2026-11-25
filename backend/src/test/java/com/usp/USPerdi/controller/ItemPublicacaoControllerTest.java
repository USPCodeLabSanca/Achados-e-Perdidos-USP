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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemPublicacaoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtEncoder jwtEncoder;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ItemRepository itemRepository;

    private String email;
    private String token;

    @BeforeEach
    void prepararUsuarioAutenticado() {
        email = "publicacao-" + UUID.randomUUID() + "@usp.br";
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmailUsp(email);
        usuario.setCampus(Campus.sp_butanta);
        usuario.setStatusConta(StatusConta.ativa);
        usuario.setFoto("https://google.test/foto.png");
        usuarioRepository.saveAndFlush(usuario);

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
    void usuarioAutenticadoPublicaAchadoAssociadoAoSeuUsuario() throws Exception {
        mockMvc.perform(multipart("/v1/items/found")
                        .part(parteDados("Chave", "Acessório"))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("encontrado"))
                .andExpect(jsonPath("$.criadorEmail").value(email));

        Item salvo = itemRepository.findAll().stream()
                .filter(item -> item.getUsuario().getEmailUsp().equals(email))
                .findFirst().orElseThrow();
        assertThat(salvo.getStatus()).isEqualTo(StatusItem.encontrado);
        assertThat(salvo.getUsuario().getEmailUsp()).isEqualTo(email);
    }

    @Test
    void usuarioAutenticadoPublicaItemPerdido() throws Exception {
        mockMvc.perform(multipart("/v1/items/lost")
                        .part(parteDados("Mochila", "Acessório"))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("perdido"));

        assertThat(itemRepository.findAll().stream()
                .filter(item -> item.getUsuario().getEmailUsp().equals(email))
                .map(Item::getStatus)).contains(StatusItem.perdido);
    }

    @Test
    void visitanteNaoAutenticadoNaoPublicaItem() throws Exception {
        mockMvc.perform(multipart("/v1/items/found").part(parteDados("Chave", "Acessório")))
                .andExpect(status().isUnauthorized());

        assertThat(itemRepository.findAll().stream()
                .noneMatch(item -> item.getUsuario().getEmailUsp().equals(email))).isTrue();
    }

    @Test
    void campoObrigatorioInvalidoRetornaQuatrocentosSemSalvar() throws Exception {
        MockPart dados = parteDados(" ", "Acessório");

        mockMvc.perform(multipart("/v1/items/found")
                        .part(dados)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errosCampos.nome").exists());

        assertThat(itemRepository.findAll().stream()
                .noneMatch(item -> item.getUsuario().getEmailUsp().equals(email))).isTrue();
    }

    private MockPart parteDados(String nome, String tipo) {
        MockPart dados = new MockPart("dados", ("{\"nome\":\"" + nome + "\",\"tipo\":\"" + tipo + "\"}")
                .getBytes(StandardCharsets.UTF_8));
        dados.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return dados;
    }
}
