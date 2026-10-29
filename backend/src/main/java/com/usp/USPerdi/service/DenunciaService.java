package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.DenunciaAtualizarDTO;
import com.usp.USPerdi.dto.DenunciaCriarNovaDTO;
import com.usp.USPerdi.dto.DenunciaJulgarDTO;
import com.usp.USPerdi.dto.DenunciaResponseDTO;
import com.usp.USPerdi.model.Denuncia;
import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.StatusDenuncia;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.DenunciaRepository;
import com.usp.USPerdi.repository.ItemRepository;
import com.usp.USPerdi.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioService usuarioService;
    private final ItemService itemService;

    // Constructor
    public DenunciaService(DenunciaRepository denunciaRepository, UsuarioService usuarioService, ItemService itemService) {
        this.denunciaRepository = denunciaRepository;
        this.usuarioService = usuarioService;
        this.itemService = itemService;
    }

    // Converte objeto em DTO para enviar ao front
    private DenunciaResponseDTO converterDenunciaParaDTO(Denuncia denuncia){

        // 1. Se item não existir, fica null
        Long idItem = null;
        String nomeItem = null;

        if (denuncia.getItemEnvolvido() != null) {
            idItem = denuncia.getItemEnvolvido().getIdItem();
            nomeItem = denuncia.getItemEnvolvido().getNome();
        }

        // 2. Monta o DTO
        return new DenunciaResponseDTO(
                denuncia.getIdDenuncia(),
                denuncia.getDescricao(),
                denuncia.getDataDenuncia(),
                denuncia.getStatus(),

                // Dados do Denunciante (Quem abriu a denúncia)
                denuncia.getUsrDenunciante().getId(),
                denuncia.getUsrDenunciante().getNome(),

                // Dados do Denunciado (O acusado)
                denuncia.getUsrDenunciado().getId(),
                denuncia.getUsrDenunciado().getNome(),

                // Dados do Item Opcional
                idItem,
                nomeItem
        );

    }


    // CREATE

        @Transactional
    public DenunciaResponseDTO criarNovaDenuncia(DenunciaCriarNovaDTO dto, String emailUspLogado){

        Usuario usrDenunciado = usuarioService.buscarPorId(dto.idDenunciado());
        Usuario usrDenunciante = usuarioService.buscarPorEmailUSP(emailUspLogado);

        // 1. Verificação de auto-denúncia
        if (usrDenunciado.getId().equals(usrDenunciante.getId())){
            throw new IllegalArgumentException("Não é permitido auto-denúncia");
        }

        // 2. Trava contra Spam: verifica se Usuário já tem denúncia ativa contra essa pessoa
            if (denunciaRepository.existsByUsrDenuncianteAndUsrDenunciadoAndStatus(usrDenunciante, usrDenunciado, StatusDenuncia.pendente)) {
                throw new IllegalArgumentException("Você já possui uma denúncia em análise contra este usuário.");
            }

        Denuncia denuncia = new Denuncia();
        denuncia.setUsrDenunciante(usrDenunciante);
        denuncia.setUsrDenunciado(usrDenunciado);
        denuncia.setDescricao(dto.descricao());

        denuncia.setDataDenuncia(Instant.now());
        denuncia.setStatus(StatusDenuncia.pendente);

        // 2. Verifica se tem item envolvido
        if (dto.idItemEnvolvido() != null){
            Item itemEnvolvido = itemService.buscarPorId(dto.idItemEnvolvido());
            denuncia.setItemEnvolvido(itemEnvolvido);
        }


        denuncia = denunciaRepository.save(denuncia);

        return converterDenunciaParaDTO(denuncia);
    }


    // READS

    // ##############################
    // METODOS INTERNOS (BACKEND)
    // ##############################

    // ##############################
    // METODOS EXTERNOS (FRONTEND)
    // ##############################

    public Page<DenunciaResponseDTO> buscaDenunciasRecebidasPorUsuario(Long idDenunciado, Pageable pageable){

        Usuario usrDenunciado = usuarioService.buscarPorId(idDenunciado);

        return denunciaRepository.findByUsrDenunciado(usrDenunciado, pageable)
                .map(this::converterDenunciaParaDTO);
    }

    public Page<DenunciaResponseDTO> buscarDenunciasFeitasPorUsuario(Long idUsuario, Pageable pageable) {
        Usuario usrDenunciante = usuarioService.buscarPorId(idUsuario);

        return denunciaRepository.findByUsrDenunciante(usrDenunciante, pageable)
                .map(this::converterDenunciaParaDTO);
    }

    public Page<DenunciaResponseDTO> buscarTodasPorStatus(StatusDenuncia status, Pageable pageable) {
        return denunciaRepository.findByStatus(status, pageable)
                .map(this::converterDenunciaParaDTO);
    }

    public Page<DenunciaResponseDTO> buscarDenunciasRecebidasPorStatus(Long idUsuario, StatusDenuncia status, Pageable pageable) {
        Usuario usrDenunciado = usuarioService.buscarPorId(idUsuario);

        return denunciaRepository.findByUsrDenunciadoAndStatus(usrDenunciado, status, pageable)
                .map(this::converterDenunciaParaDTO);
    }

    // UPDATE

    @Transactional
    public DenunciaResponseDTO atualizarDenunciaExistente(Long idDenuncia, String emailUspLogado, DenunciaAtualizarDTO dto){

        // 1. Encontrar denúncia no BD
        Denuncia denuncia = denunciaRepository.findById(idDenuncia)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia inexistente de ID: " + idDenuncia));

        // 2. Verifica se denuncia ja nao foi resolvida
        if (!denuncia.getStatus().equals(StatusDenuncia.pendente)){
            throw new IllegalArgumentException("Denúncia já avaliada de ID: " + idDenuncia);
        }

        // 3. Trava de segurança
        Usuario usrLogado = usuarioService.buscarPorEmailUSP(emailUspLogado);
        if (!denuncia.getUsrDenunciante().getId().equals(usrLogado.getId())){
            throw new SecurityException("Você não tem permissão para editar essa denúncia.");
        }

        // 4. Descricao nova valida
        if (dto.descricao().isBlank()){
            throw new IllegalArgumentException("Nova descrição em branco ou não está na faixa de caracteres aceita");
        }

        denuncia.setDescricao(dto.descricao());
        denunciaRepository.save(denuncia);

        return converterDenunciaParaDTO(denuncia);
    }

    // DELETE

    @Transactional
    public void deletarDenuncia(String emailUspLogado, Long idDenuncia){

        // 1. Encontrar denúncia no BD
        Denuncia denuncia = denunciaRepository.findById(idDenuncia)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia inexistente de ID: " + idDenuncia));

        // 2. Trava de segurança
        Usuario usrLogado = usuarioService.buscarPorEmailUSP(emailUspLogado);
        if (!denuncia.getUsrDenunciante().getId().equals(usrLogado.getId())){
            throw new SecurityException("Você não tem permissão para deletar essa denúncia.");
        }

        // 3. Trava de Status: Não apagar histórico de denúncias já julgadas
        if (!denuncia.getStatus().equals(StatusDenuncia.pendente)) {
            throw new IllegalArgumentException("Não é possível deletar uma denúncia que já foi avaliada pelos administradores.");
        }

        denunciaRepository.delete(denuncia);
    }


    public DenunciaResponseDTO alterarStatusDenuncia(Long idDenuncia, DenunciaJulgarDTO dto) {
        // OBS: a verificação de administrador ficará no controller (role)

        // 1. Encontrar denúncia no BD
        Denuncia denuncia = denunciaRepository.findById(idDenuncia)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia inexistente de ID: " + idDenuncia));

        // 2. Evita transações desnecessárias no banco
        if (denuncia.getStatus().equals(dto.novoStatus())) {
            throw new IllegalArgumentException("A denúncia já se encontra no status: " + dto.novoStatus());
        }

        // 3. Atualiza o status
        denuncia.setStatus(dto.novoStatus());
        denuncia = denunciaRepository.save(denuncia);

        // 4. Converte e retorna
        return converterDenunciaParaDTO(denuncia);
    }
}
