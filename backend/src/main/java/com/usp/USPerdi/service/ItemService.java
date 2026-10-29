package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.ItemCadastrarNovoDTO;
import com.usp.USPerdi.dto.ItemExistenteAtualizarDTO;
import com.usp.USPerdi.dto.ItemResponseDTO;
import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.StatusItem;
import com.usp.USPerdi.model.Usuario;
import com.usp.USPerdi.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;

    // Constructor


    public ItemService(ItemRepository itemRepository, UsuarioService usuarioService, FileStorageService fileStorageService) {
        this.itemRepository = itemRepository;
        this.usuarioService = usuarioService;
        this.fileStorageService = fileStorageService;
    }

    private ItemResponseDTO converterParaDTO(Item item) {
        return new ItemResponseDTO(
                item.getIdItem(),
                item.getNome(),
                item.getFoto(),
                item.getLocal(),
                item.getStatus(),
                item.getDescricao(),
                item.getTipo(),
                item.getDataOcorrencia(),

                // Puxamos apenas o que o frontend precisa do usuário
                item.getUsuario().getNome(),
                item.getUsuario().getEmailUsp(),
                item.getUsuario().getTelefone(),
                item.getUsuario().getFoto()
        );
    }



    // CREATE

    // ####################
    // Metodos Internos (BACKEND)
    // ####################

    public Item buscarPorId(Long id){
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado com o ID: " + id));}


    // ####################
    // METODOS EXTERNOS (FRONTEND)
    // ####################

    @Transactional
    public ItemResponseDTO cadastrarNovoItem(String emailUsp, ItemCadastrarNovoDTO dto, MultipartFile imagem, StatusItem status){

        // 1. Encontra usuario no BD
        Usuario usr = usuarioService.buscarPorEmailUSP(emailUsp); // Exception ja lancada no service

        // 2. Valida imagem
        if (imagem == null || imagem.isEmpty()){
            throw new IllegalArgumentException("A foto do item é obrigatória para o cadastro.");
        }

        // 3. Converte imagem em URL
        String imgURL = fileStorageService.salvarArquivo(imagem);

        Item item = new Item();

        item.setNome(dto.nome());
        item.setDescricao(dto.descricao());
        item.setLocal(dto.local());
        item.setTipo(dto.tipo());
        item.setStatus(status);
        item.setUsuario(usr);
        item.setDataOcorrencia(dto.dataOcorrencia());
        item.setFoto(imgURL);

        Item itemSalvo =  itemRepository.save(item);
        return converterParaDTO(itemSalvo);
    }

    // READ

    public Page<ItemResponseDTO> buscaTodosPorNome(String nome, Pageable pageable){
        return itemRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::converterParaDTO);
    }

    public Page<ItemResponseDTO> buscaTodosPorUsuario(String emailUsp, Pageable pageable){
        Usuario usr = usuarioService.buscarPorEmailUSP(emailUsp);
        return itemRepository.findByUsuario(usr, pageable)
                .map(this::converterParaDTO);
    }

    public Page<ItemResponseDTO> buscaMaisRecentesPorStatus(StatusItem status, Pageable pageble){
        return itemRepository.findByStatusOrderByDataOcorrenciaDesc(status, pageble)
                .map(this::converterParaDTO);
    }

    public Page<ItemResponseDTO> buscarPorLocal(String local, Pageable pageable){
        return itemRepository.findByLocalContainingIgnoreCase(local, pageable)
                .map(this::converterParaDTO);
    }

    // UPDATE

    @Transactional
    public ItemResponseDTO atualizarItem(Long idItem, String emailUspLogado, ItemExistenteAtualizarDTO dto){

        // 1. Busca item cadastrado
        Item item = itemRepository.findById(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        // 2. Apenas o dono pode editar o item
        if (!item.getUsuario().getEmailUsp().equals(emailUspLogado)) {
            throw new SecurityException("Você não tem permissão para editar este item.");
        }

        // 3. Atualiza apenas os campos que o frontend enviou (ignora os nulos)
        if (dto.nome() != null && !dto.nome().isBlank()) {
            item.setNome(dto.nome());
        }

        if (dto.local() != null) {
            item.setLocal(dto.local());
        }

        if (dto.status() != null) {
            item.setStatus(dto.status());
        }

        if (dto.descricao() != null) {
            item.setDescricao(dto.descricao());
        }

        if (dto.tipo() != null && !dto.tipo().isBlank()) {
            item.setTipo(dto.tipo());
        }

        if (dto.dataOcorrencia() != null) {
            item.setDataOcorrencia(dto.dataOcorrencia());
        }

        // 4. Salva e devolve o DTO de leitura formatado e blindado
        itemRepository.save(item);
        return converterParaDTO(item);

    }

    @Transactional
    public ItemResponseDTO atualizarFotoItem(Long idItem, String emailUspLogado, MultipartFile novaImagem) {

        // 1. Busca o item no banco
        Item item = itemRepository.findById(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        // Apenas o dono pode alterar a foto
        if (!item.getUsuario().getEmailUsp().equals(emailUspLogado)) {
            throw new SecurityException("Você não tem permissão para alterar a foto deste item.");
        }

        // 3. Valida se a imagem realmente foi enviada e não está vazia
        if (novaImagem == null || novaImagem.isEmpty()) {
            throw new IllegalArgumentException("O arquivo da nova foto é obrigatório.");
        }

        // 4. Faz o upload da nova imagem
        String novaUrlFoto = fileStorageService.salvarArquivo(novaImagem);

        fileStorageService.apagarArquivo(item.getFoto());

        // 5. Substitui a URL na entidade e salva
        item.setFoto(novaUrlFoto);
        itemRepository.save(item);

        // 6. Devolve o item formatado em DTO para a tela já atualizar a foto em tempo real
        return converterParaDTO(item);
    }

    @Transactional
    public void excluirItem(Long idItem, String emailUspLogado) {

        // 1. Busca o item no banco
        Item item = itemRepository.findById(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        // 2. Apenas o dono pode excluir o item
        if (!item.getUsuario().getEmailUsp().equals(emailUspLogado)) {
            throw new SecurityException("Você não tem permissão para excluir este item.");
        }

        // 3. Exclui o item do banco de dados
        itemRepository.delete(item);

        fileStorageService.apagarArquivo(item.getFoto());
    }

}
