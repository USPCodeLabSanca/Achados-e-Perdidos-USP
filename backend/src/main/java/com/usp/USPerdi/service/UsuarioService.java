package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.UsuarioAtualizarPerfilDTO;
import com.usp.USPerdi.dto.UsuarioCompletarCadastroDTO;
import com.usp.USPerdi.dto.UsuarioResponseDTO;
import com.usp.USPerdi.model.Campus;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UsuarioService {

    // TODO: Trocar URL depois
    @Value("${DEFAULT_USER_PHOTO:https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg}")
    private String defaultUserPhoto;

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usr) {
        return new UsuarioResponseDTO(
                usr.getId(),
                usr.getNome(),
                usr.getEmailUsp(),
                usr.getFoto(),
                usr.getTelefone(),
                usr.getCampus(),
                usr.getStatusConta(),
                usr.getDataCadastro()
        );
    }


    // CREATE
    @Transactional
    public UsuarioResponseDTO completarCadastro(@Valid UsuarioCompletarCadastroDTO dto, String fotoDoGoogle){

        // 1. Verificar no BD se ja ha alguem cadastrado com esse email usp
        if (usuarioRepository.findByEmailUsp(dto.emailUsp()).isPresent()){
            throw new IllegalArgumentException("Este e-mail USP já possui cadastro no sistema.");
        }

        // 2. Instanciar um usuário com as informações do DTO
        Usuario usr = dto.toUsuario();

        // 2.1 Atribui a foto oficial que veio do token do Google
        // Se por algum motivo o Google não mandar, podemos definir um fallback (opcional)
        usr.setFoto(fotoDoGoogle != null ? fotoDoGoogle : defaultUserPhoto);

        // 3. Salva novo user no BD
        Usuario usrSalvo = usuarioRepository.save(usr);

        return converterParaDTO(usrSalvo);
    }

    // READ

    // ####################
    // Metodos Internos (BACKEND)
    // ####################

    // Retornamos USUARIO porque eh utilizado no ItemService
    public Usuario buscarPorEmailUSP(String emailUsp){
        return usuarioRepository.findByEmailUsp(emailUsp)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail: " + emailUsp));
    }

    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
    }



    // ####################
    // METODOS EXTERNOS (FRONTEND)
    // ####################
    // Para exibicao do perfil no front-end
    public UsuarioResponseDTO obterPerfilUsuario(String emailUsp) {
        Usuario usr = buscarPorEmailUSP(emailUsp);
        return converterParaDTO(usr);
    }

    public Page<UsuarioResponseDTO> buscaPorNome(String nome, Pageable pageable){
        return usuarioRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::converterParaDTO);
    }

    public Page<UsuarioResponseDTO> buscaPorStatusDaConta(StatusConta status, Pageable pageable){
        return usuarioRepository.findByStatusConta(status, pageable)
                .map(this::converterParaDTO);
    }

    // UPDATE
    @Transactional
    public UsuarioResponseDTO atualizarPerfil(String emailUsp, UsuarioAtualizarPerfilDTO dto) {
        // Obs: emailUsp será recebido do controller, pelo Spring Security
        // Nao usa o ID por questao de segurança

        // TODO: futuramente pensar no update de imagem de perfil via upload da galera/tirar foto

        Usuario usr = buscarPorEmailUSP(emailUsp);

        if (dto.nome() != null && !(dto.nome().isBlank())) {
            usr.setNome(dto.nome());
        }

        if (dto.campus() != null) {
            usr.setCampus(dto.campus());
        }

        if (dto.telefone() != null) {
            // Permite apagar o telefone enviando uma string vazia, ou atualizar para um novo
            usr.setTelefone(dto.telefone().isBlank() ? null : dto.telefone());
        }

        // Salva e devolve o DTO
        usuarioRepository.save(usr);
        return converterParaDTO(usr);
    }

    // DELETE
    // Exclusão lógica (Soft Delete)
    @Transactional
    public void desativarConta(String emailUsp) {
        Usuario usr = buscarPorEmailUSP(emailUsp);
        usr.setStatusConta(StatusConta.inativa);
    }
}
