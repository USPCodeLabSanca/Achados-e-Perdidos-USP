package com.usp.USPerdi.controller;

import com.usp.USPerdi.dto.ItemCadastrarNovoDTO;
import com.usp.USPerdi.dto.ItemExistenteAtualizarDTO;
import com.usp.USPerdi.dto.ItemResponseDTO;
import com.usp.USPerdi.model.StatusItem;
import com.usp.USPerdi.service.ItemService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // CREATE

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponseDTO> criarItem(
            @Valid @RequestPart("dados") ItemCadastrarNovoDTO itemDTO,
            @RequestPart("foto") MultipartFile foto,
            @RequestParam("status") StatusItem status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String emailUsp = jwt.getClaimAsString("email");

        return ResponseEntity.status(201).body(itemService.cadastrarNovoItem(
                emailUsp,
                itemDTO,
                foto,
                status
        ));
    }

    // READ

    @GetMapping("/busca/nome")
    public ResponseEntity<Page<ItemResponseDTO>> buscaPorNome(
            @RequestParam("nome") String nome,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(
                itemService.buscaTodosPorNome(nome, pageable)
        );
    }

    @GetMapping("/busca/usuario")
    public ResponseEntity<Page<ItemResponseDTO>> buscaPorUsuario(
            @RequestParam("email") String email,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(
                itemService.buscaTodosPorUsuario(email, pageable)
        );
    }

    @GetMapping("/busca/status")
    public ResponseEntity<Page<ItemResponseDTO>> buscaPorStatus(
            @RequestParam("status") StatusItem statusItem,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(
                itemService.buscaMaisRecentesPorStatus(statusItem, pageable)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponseDTO>> buscaPorLocal(
            @RequestParam("local") String local,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(
                itemService.buscarPorLocal(local, pageable)
        );
    }

    // Update

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> atualizarItem(
            @PathVariable Long id,
            @RequestBody ItemExistenteAtualizarDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaimAsString("email");
        ItemResponseDTO itemAtualizado = itemService.atualizarItem(
            id, email, dto
        );
        return ResponseEntity.ok().body(itemAtualizado);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponseDTO> atualizarFotoItem(
            @PathVariable Long id,
            @RequestPart("foto") MultipartFile foto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaimAsString("email");
        ItemResponseDTO itemAtualizado = itemService.atualizarFotoItem(
                id, email, foto
        );
        return ResponseEntity.ok().body(itemAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removerItem(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaimAsString("email");
        itemService.excluirItem(id, email);
        return ResponseEntity.ok().build();
    }
}
