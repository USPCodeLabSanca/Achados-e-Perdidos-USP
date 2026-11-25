package com.usp.USPerdi.service;

import com.usp.USPerdi.exception.ArmazenamentoArquivoException;
import com.usp.USPerdi.exception.RegraDeNegocioException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale; 
import java.util.Set;
import java.util.UUID;


@Service
public class FileStorageService {

    //Define quais extensões de arquivos são permitidas para upload de fotos
    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // Define que a pasta vai se chamar "uploads" na raiz do backend
    private Path pastaUploads = Paths.get("uploads");

    // TODO: trocar URL BASE
    private final String apiBaseUrl;

    public FileStorageService(
            @Value("${API_BASE_URL:http://localhost:8080}") String apiBaseUrl,
            @Value("${app.uploads-dir:uploads}") String pastaUploads
    ) {
        this.apiBaseUrl = apiBaseUrl;
        this.pastaUploads = Paths.get(pastaUploads).toAbsolutePath().normalize();

        try {
            // Cria a pasta automaticamente quando o Spring iniciar
            Files.createDirectories(this.pastaUploads);
        } catch (IOException e) {
            throw new ArmazenamentoArquivoException("Não foi possível criar a pasta para upload de imagens.", e);
        }
    }

    public String salvarArquivo(MultipartFile arquivo) {
        // Validações iniciais do arquivo
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraDeNegocioException("O arquivo da imagem é obrigatório.");
        }
    
        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal == null || nomeOriginal.isBlank() || !nomeOriginal.contains(".")) {
            throw new RegraDeNegocioException("Nome de arquivo inválido ou sem extensão.");
        }

        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf(".")).toLowerCase(Locale.ROOT);
        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new RegraDeNegocioException("Formato de imagem não permitido. Utilize JPG, JPEG, PNG ou WEBP.");
        }

        String contentType = arquivo.getContentType();
        if (contentType != null && !contentType.isBlank()
                && !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new RegraDeNegocioException("Tipo de conteúdo inválido. Envie uma imagem válida (JPG, PNG ou WEBP).");
        }
        try {

            // 2. Gera um nome único aleatório para não haver conflito
            String nomeNovo = UUID.randomUUID().toString() + extensao;

            // 3. Define o caminho completo onde será salvo (uploads/nome-novo.jpg)
            Path arquivoDestino = pastaUploads.resolve(nomeNovo);

            // 4. Copia os bytes da requisição para o disco do servidor
            Files.copy(arquivo.getInputStream(), arquivoDestino, StandardCopyOption.REPLACE_EXISTING);

            // 5. Retorna a URL que será salva no banco de dados e usada pelo frontend
            return apiBaseUrl + "/uploads/" + nomeNovo;

        } catch (IOException e) {
            throw new ArmazenamentoArquivoException("Falha ao armazenar a imagem.", e);
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
        } catch (InvalidPathException e) {
            System.err.println("Aviso: Caminho de arquivo inválido ao tentar apagar: " + urlArquivo);
        } catch (IOException e) {
            // Apenas exibimos o erro no log.
            // Não lançamos RuntimeException aqui para não dar "Rollback" no banco de dados
            System.err.println("Aviso: Falha ao apagar o arquivo do disco: " + urlArquivo);
            e.printStackTrace();
        }
    }
}