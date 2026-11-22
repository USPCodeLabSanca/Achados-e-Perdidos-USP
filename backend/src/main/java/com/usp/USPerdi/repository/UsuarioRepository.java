package com.usp.USPerdi.repository;

import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.StatusConta;
import com.usp.USPerdi.model.StatusItem;
import com.usp.USPerdi.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca de usuario pelo email_usp
    Optional<Usuario> findByEmailUsp(String emailUsp);

    // Busca aproximada (LIKE %nome%) ignorando maiúsculas e minúsculas
    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca usuarios por um status especifico
    Page<Usuario> findByStatusConta(StatusConta status, Pageable pageable);

}
