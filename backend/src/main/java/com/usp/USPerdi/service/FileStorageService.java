package com.usp.USPerdi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Define que a pasta vai se chamar "uploads" na raiz do backend
    private final Path pastaUploads = Paths.get("uploads");

    // TODO: trocar URL BASE
    @Value("${API_BASE_URL:http://localhost:8080}")
    private String apiBaseUrl;

    public FileStorageService() {
        try {
            // Cria a pasta automaticamente quando o Spring iniciar
            Files.createDirectories(pastaUploads);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar a pasta para upload de imagens.", e);
        }
    }

    public String salvarArquivo(MultipartFile arquivo) {
        try {
            // 1. Pega a extensão original do arquivo (ex: .png, .jpg)
            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = nomeOriginal != null ? nomeOriginal.substring(nomeOriginal.lastIndexOf(".")) : ".jpg";

            // 2. Gera um nome único aleatório para não haver conflito
            String nomeNovo = UUID.randomUUID().toString() + extensao;

            // 3. Define o caminho completo onde será salvo (uploads/nome-novo.jpg)
            Path arquivoDestino = pastaUploads.resolve(nomeNovo);

            // 4. Copia os bytes da requisição para o disco do servidor
            Files.copy(arquivo.getInputStream(), arquivoDestino, StandardCopyOption.REPLACE_EXISTING);

            // 5. Retorna a URL que será salva no banco de dados e usada pelo frontend
            return apiBaseUrl + "/uploads/" + nomeNovo;

        } catch (IOException e) {
            throw new RuntimeException("Falha ao armazenar a imagem.", e);
        }
    }

    // Apagar do disco
    public void apagarArquivo(String urlArquivo) {
        if (urlArquivo == null || urlArquivo.trim().isEmpty()) {
            return; // Se não tem URL não faz nada
        }

        try {
            // 1. Extrai apenas o nome do arquivo da URL.
            // Pega tudo que vem depois da última barra ("/")
            String nomeArquivo = urlArquivo.substring(urlArquivo.lastIndexOf("/") + 1);

            // 2. Aponta para o arquivo exato dentro da pasta de uploads
            Path arquivoFisico = pastaUploads.resolve(nomeArquivo);

            // 3. Apaga o arquivo apenas se ele existir (deleteIfExists).
            // Isso evita que a API quebre (Erro 500) caso o arquivo já tenha sido deletado manualmente do servidor.
            Files.deleteIfExists(arquivoFisico);

        } catch (IOException e) {
            // Apenas exibimos o erro no log.
            // Não lançamos RuntimeException aqui para não dar "Rollback" no banco de dados
            System.err.println("Aviso: Falha ao apagar o arquivo do disco: " + urlArquivo);
            e.printStackTrace();
        }
    }
}