package com.usp.USPerdi.service;

import com.usp.USPerdi.dto.ItemResponseDTO;
import com.usp.USPerdi.model.Item;
import com.usp.USPerdi.repository.ItemRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ItemService itemService;

    @Test
    void deveCadastrarNovoItem() {

    }

}