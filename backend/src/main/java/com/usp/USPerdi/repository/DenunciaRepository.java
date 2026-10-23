package com.usp.USPerdi.repository;

import com.usp.USPerdi.model.Denuncia;
import com.usp.USPerdi.model.StatusDenuncia;
import com.usp.USPerdi.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    // Busca as denuncias que um usuário recebeu (histórico de infrações)
    Page<Denuncia> findByUsrDenunciado(Usuario usrDenunciado, Pageable pageable);

    // Busca as denuncias que um usuário fez (histórico de colaboração)
    Page<Denuncia> findByUsrDenunciante(Usuario usrDenunciante, Pageable pageable);

    // Busca todas as denúncias por status (ex: listar todas as "pendentes" para os admins)
    Page<Denuncia> findByStatus(StatusDenuncia status, Pageable pageable);

    // Busca denúncias contra um usuário e que ainda estão pendentes
    Page<Denuncia> findByUsrDenunciadoAndStatus(Usuario denunciado, StatusDenuncia status, Pageable pageable);

}
