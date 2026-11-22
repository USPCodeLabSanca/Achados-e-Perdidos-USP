package com.usp.USPerdi.repository;

import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.Recupera;
import com.usp.USPerdi.model.RecuperaId;
import com.usp.USPerdi.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecuperaRepository extends JpaRepository<Recupera, RecuperaId> {

    // Busca o histórico de itens recuperados por um usuário específico
    Page<Recupera> findByUsuario(Usuario usuario, Pageable pageable);

    // Busca o registro de recuperação de um item específico
    Page<Recupera> findByItem(Item item, Pageable pageable);
}