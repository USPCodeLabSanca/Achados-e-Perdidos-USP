package com.usp.USPerdi.controller;

import com.usp.USPerdi.dto.UsuarioAtualizarPerfilDTO;
import com.usp.USPerdi.dto.UsuarioCompletarCadastroDTO;
import com.usp.USPerdi.dto.UsuarioResponseDTO;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Rotas

    // CREATE
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(
            @Valid @RequestBody UsuarioCompletarCadastroDTO dto,
            @RequestHeader(value = "foto-google", required = false) String fotoGoogle){

        return ResponseEntity.status(201).body(usuarioService.completarCadastro(dto, fotoGoogle));
    }


    // READ

    // Busca por emailUsp. Retorna uma entidade DTO
    @GetMapping("/{emailUsp}")
    public ResponseEntity<UsuarioResponseDTO> obterUsuario(
            @PathVariable String emailUsp
    ){
        return ResponseEntity.status(200).body(usuarioService.obterPerfilUsuario(emailUsp));
    }


    // Busca por nome. Retorna uma Page
    @GetMapping("/busca")
    public ResponseEntity<Page<UsuarioResponseDTO>> buscaPorNome(
            @RequestParam String nome, // pega o valor depois do ?nome=
            Pageable pageable
    ){
        return ResponseEntity.status(200).body(usuarioService.buscaPorNome(nome, pageable));
    }

    // Busca por status. Retorna uma Page
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<UsuarioResponseDTO>> buscaPorStatus(
            @PathVariable StatusConta status,
            Pageable pageable
            ){

        return ResponseEntity.status(200).body(usuarioService.buscaPorStatusDaConta(status, pageable));
    }


    // UPDATE

    @PatchMapping("/{emailUsp}")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(
            @PathVariable String emailUsp,
            @Valid @RequestBody UsuarioAtualizarPerfilDTO dto
            ){

        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizarPerfil(emailUsp, dto);

        return ResponseEntity.status(200).body(usuarioAtualizado);
    }

    // DELETE

    @DeleteMapping("/{emailUsp}")
    public ResponseEntity<Void> desativarConta(
            @PathVariable String emailUsp
    ){
        usuarioService.desativarConta(emailUsp);

        // Retorna Status 204 (No Content) indicando que deu certo e a bandeja está vazia
        return ResponseEntity.noContent().build();
    }


}
