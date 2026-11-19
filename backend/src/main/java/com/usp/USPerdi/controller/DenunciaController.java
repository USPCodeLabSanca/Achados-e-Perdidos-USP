package com.usp.USPerdi.controller;

import com.usp.USPerdi.dto.DenunciaAtualizarDTO;
import com.usp.USPerdi.dto.DenunciaCriarNovaDTO;
import com.usp.USPerdi.dto.DenunciaJulgarDTO;
import com.usp.USPerdi.dto.DenunciaResponseDTO;
import com.usp.USPerdi.model.StatusDenuncia;
import com.usp.USPerdi.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/denuncias")
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @PostMapping
    public ResponseEntity<DenunciaResponseDTO> criarNovaDenuncia(
            @RequestBody @Valid DenunciaCriarNovaDTO dto,
            @AuthenticationPrincipal Jwt jwt
            ) {
        String email = jwt.getClaimAsString("email");
        return ResponseEntity.status(201).body(
                denunciaService.criarNovaDenuncia(
                        dto,
                        email
                )
        );
    }

    @GetMapping("busca/usuario/denunciado")
    public ResponseEntity<Page<DenunciaResponseDTO>> buscarPorUsuarioDenunciado(
            @RequestParam("id") Long id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                denunciaService.buscaDenunciasRecebidasPorUsuario(id, pageable)
        );
    }

    @GetMapping("busca/usuario/denunciante")
    public ResponseEntity<Page<DenunciaResponseDTO>> buscarPorUsuarioDenunciante(
            @RequestParam("id") Long id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                denunciaService.buscarDenunciasFeitasPorUsuario(id, pageable)
        );
    }

    @GetMapping("busca/status")
    public ResponseEntity<Page<DenunciaResponseDTO>> buscaTodasPorStatus(
            @RequestParam("status") StatusDenuncia statusDenuncia,
            Pageable pageable
            ) {
        return ResponseEntity.ok(
                denunciaService.buscarTodasPorStatus(statusDenuncia, pageable)
        );
    }

    @GetMapping("busca/status/usuario")
    public ResponseEntity<Page<DenunciaResponseDTO>> buscaDenunciaPorUsuarioEStatus(
            @RequestParam("status") StatusDenuncia statusDenuncia,
            @RequestParam("id") Long id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                denunciaService.buscarDenunciasRecebidasPorStatus(id, statusDenuncia, pageable)
        );
    }

    @PatchMapping("{id}")
    public ResponseEntity<DenunciaResponseDTO> atualizarDenuncia(
            @PathVariable Long id,
            @RequestBody @Valid DenunciaAtualizarDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String email = jwt.getClaimAsString("email");
        DenunciaResponseDTO denunciaAtualizada = denunciaService.atualizarDenunciaExistente(id, email, dto);
        return ResponseEntity.ok(denunciaAtualizada);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DenunciaResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestBody DenunciaJulgarDTO dto,
            @AuthenticationPrincipal Jwt jwt
            ) {
        // TODO: Identificar se é administrador
        // Provavelmente precisa mexer no token
        DenunciaResponseDTO denunciaAtualizada = denunciaService.alterarStatusDenuncia(id, dto);
        return ResponseEntity.ok(denunciaAtualizada);
    }
}
