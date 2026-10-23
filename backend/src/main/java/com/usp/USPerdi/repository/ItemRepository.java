package com.usp.USPerdi.repository;

import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.model.StatusItem;
import com.usp.USPerdi.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Busca aproximada (LIKE %nome%) ignorando maiúsculas e minúsculas.
    // Busca quantidade X de acordo com o solicitado pelo front
    Page<Item> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca todos itens registrados por um usuario especifico
    Page<Item> findByUsuario(Usuario usuario, Pageable pageable);

    // Busca itens filtrando pelo status e ordenando pela data de ocorrência (mais recentes primeiro)
    Page<Item> findByStatusOrderByDataOcorrenciaDesc(StatusItem status, Pageable pageable);

    // Busca itens filtrando pelo status e ordenando pela data de registro (mais recentes primeiro)
    Page<Item> findByStatusOrderByDataRegistroDesc(StatusItem status, Pageable pageable);

    // Busca itens por um local aproximado
    Page<Item> findByLocalContainingIgnoreCase(String local, Pageable pageable);

}
