package com.usp.USPerdi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @TempDir
    private Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setup() {
        fileStorageService = new FileStorageService("teste", tempDir.toString());
    }

    @Test
    void deveSalvarArquivo() throws IOException {
        String texto = "haahahahah";
        InputStream stream = new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8));

        final MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename())
                .thenReturn("arquivoShow.png");
        when(mockFile.getInputStream())
                .thenReturn(stream);

        String url = fileStorageService.salvarArquivo(mockFile);

        String nomeNovo = url.substring(url.lastIndexOf("/") + 1);

        Assertions.assertTrue(url.startsWith("teste/uploads/"));
        Assertions.assertTrue(url.endsWith(".png"));
        Assertions.assertTrue(Files.exists(tempDir.resolve(nomeNovo)));
        Assertions.assertArrayEquals(texto.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(tempDir.resolve(nomeNovo)));
    }

    @Test
    void deveFalharSalvarArquivo_ExtensaoMaliciosa() throws IOException {
        // Não deveria salvar arquivos que não são de imagem!
        // pode ser uma falha de segurança
        String texto = "haahahahah";
        InputStream stream = new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8));

        final MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename())
                .thenReturn("arquivoShow.exe");
        when(mockFile.getInputStream())
                .thenReturn(stream);

        Assertions.assertThrows(RuntimeException.class,
                () -> fileStorageService.salvarArquivo(mockFile));

        Assertions.assertTrue(Files.list(tempDir).toList().isEmpty());
    }

    @Test
    void deveFalharSalvarArquivo_SemExtensao() throws IOException {
        // Não deveria salvar arquivos que não são de imagem!
        // pode ser uma falha de segurança
        String texto = "haahahahah";
        InputStream stream = new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8));

        final MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename())
                .thenReturn("arquivoShow");
        when(mockFile.getInputStream())
                .thenReturn(stream);

        Assertions.assertThrows(RuntimeException.class,
                () -> fileStorageService.salvarArquivo(mockFile));

        Assertions.assertTrue(Files.list(tempDir).toList().isEmpty());
    }

    @Test
    void deveApagarArquivo() throws IOException {
        Files.createFile(tempDir.resolve("teste"));
        Assertions.assertTrue(Files.exists(tempDir.resolve("teste")));

        fileStorageService.apagarArquivo("teste");

        Assertions.assertFalse(Files.exists(tempDir.resolve("teste")));
    }

    @Test
    void deveApagarArquivo_ArquivoInexistente() throws IOException {
        fileStorageService.apagarArquivo("teste");

        Assertions.assertFalse(Files.exists(tempDir.resolve("teste")));
    }

    @Test
    void deveFalharApagarArquivo_CaminhoProibido() throws IOException {
        Files.createDirectory(tempDir.resolve("pastaSuperImportante"));
        Files.createFile(tempDir.resolve("pastaSuperImportante/arquivoconfidencial.txt"));
        Assertions.assertTrue(Files.exists(tempDir.resolve("pastaSuperImportante/arquivoconfidencial.txt")));

        fileStorageService.apagarArquivo("pastaSuperImportante/arquivoconfidencial.txt");

        Assertions.assertTrue(Files.exists(tempDir.resolve("pastaSuperImportante/arquivoconfidencial.txt")));
    }

    @Test
    void deveFalharApagarArquivo_ApagandoPasta() throws IOException {
        Files.createDirectory(tempDir.resolve("pastaSuperImportante"));
        Files.createFile(tempDir.resolve("pastaSuperImportante/arquivoconfidencial.txt"));
        Assertions.assertTrue(Files.exists(tempDir.resolve("pastaSuperImportante/arquivoconfidencial.txt")));

        fileStorageService.apagarArquivo("pastaSuperImportante");

        Assertions.assertTrue(Files.exists(tempDir.resolve("pastaSuperImportante/arquivoconfidencial.txt")));
    }

    @Test
    void deveFalharApagarArquivo_InjecaoComando() throws IOException {
        for(int i = 0; i < 100; i++) {
            Files.createFile(tempDir.resolve("teste" + i));
            Assertions.assertTrue(Files.exists(tempDir.resolve("teste" + i)));
        }

        // * representando todos arquivos da pasta
        // não deve ser permitido!
        fileStorageService.apagarArquivo("*");

        Assertions.assertEquals(100, Files.list(tempDir).toList().size());
    }

}
