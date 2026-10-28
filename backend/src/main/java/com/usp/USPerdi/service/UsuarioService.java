package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.UsuarioAtualizarPerfilDTO;
import com.usp.USPerdi.dto.UsuarioCompletarCadastroDTO;
import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    // Trocar URL depois
    @Value("${DEFAULT_USER_PHOTO:https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg}")
    private String defaultUserPhoto;

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CREATE
    @Transactional
    public Usuario completarCadastro(UsuarioCompletarCadastroDTO dto, String fotoDoGoogle){

        // 1. Verificar no BD se ja ha alguem cadastrado com esse email usp
        if (usuarioRepository.findByEmailUsp(dto.emailUsp()).isPresent()){
            throw new IllegalArgumentException("Este e-mail USP já possui cadastro no sistema.");
        }

        // 2. Inicializar os campos
        Usuario usr = new Usuario();
        usr.setNome(dto.nome());
        usr.setCampus(dto.campus());
        usr.setEmailUsp(dto.emailUsp());
        usr.setTelefone(dto.telefone());
        usr.setStatusConta(StatusConta.ativa);

        // 2.1 Atribui a foto oficial que veio do token do Google
        // Se por algum motivo o Google não mandar, podemos definir um fallback (opcional)
        usr.setFoto(fotoDoGoogle != null ? fotoDoGoogle : defaultUserPhoto);


        // 3. Salva novo user no BD
        return usuarioRepository.save(usr);
    }

    // READ
    public Usuario buscarPorEmailUSP(String emailUsp){
        return usuarioRepository.findByEmailUsp(emailUsp)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail: " + emailUsp));
    }

    public Page<Usuario> buscaPorNome(String nome, Pageable pageable){
        // Caso nao encontre ninguem, retorna uma pagina vazia (lista vazia)
        return usuarioRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Page<Usuario> buscaPorStatusDaConta(StatusConta status, Pageable pageable){
        return usuarioRepository.findByStatusConta(status, pageable);
    }

    // UPDATE
    @Transactional
    public Usuario atualizarPerfil(String emailUsp, UsuarioAtualizarPerfilDTO dto){
        // Obs: emailUsp será recebido do controller, pelo Spring Security
        // Nao usa o ID por questao de segurança

        // TODO: futuramente pensar no update de imagem de perfil via upload da galera/tirar foto

        Usuario usr = buscarPorEmailUSP(emailUsp);

        if (dto.nome() != null && !(dto.nome().isBlank())){
            usr.setNome(dto.nome());
        }

        if (dto.campus() != null){
            usr.setCampus(dto.campus());
        }

        if (dto.telefone() != null) {
            // Permite apagar o telefone enviando uma string vazia, ou atualizar para um novo
            usr.setTelefone(dto.telefone().isBlank() ? null : dto.telefone());
        }

        return usr;
    }

    // DELETE
    // Exclusão lógica (Soft Delete)
    @Transactional
    public void desativarConta(String emailUsp) {
        Usuario usr = buscarPorEmailUSP(emailUsp);
        usr.setStatusConta(StatusConta.inativa);
    }
}
