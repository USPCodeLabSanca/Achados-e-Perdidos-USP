package com.usp.USPerdi.exception;

import com.usp.USPerdi.dto.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Recurso não encontrado -> 404 Not Found
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 2. Conflito / duplicidade -> 409 Conflict
    @ExceptionHandler(ConflitoDeRecursoException.class)
    public ResponseEntity<ErroResponseDTO> handleConflitoDeRecurso(
            ConflitoDeRecursoException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    // 3. Sem permissão para alterar/excluir -> 403 Forbidden
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponseDTO> handleAcessoNegado(
            AcessoNegadoException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    // 4. Violação de regra de negócio -> 400 Bad Request
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponseDTO> handleRegraDeNegocio(
            RegraDeNegocioException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Regra de negócio violada",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(erro);
    }

    // 5. Falha física ao salvar imagem no servidor -> 500 Internal Server Error
    @ExceptionHandler(ArmazenamentoArquivoException.class)
    public ResponseEntity<ErroResponseDTO> handleArmazenamentoArquivo(
            ArmazenamentoArquivoException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro ao processar arquivo",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    // 6. Erros de validação dos DTOs (@Valid) -> 400 Bad Request com mapa de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidacaoCampos(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errosCampos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errosCampos.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage())
        );

        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais campos estão inválidos.",
                request.getRequestURI(),
                errosCampos
        );
        return ResponseEntity.badRequest().body(erro);
    }

    // 7. Erros de validação em parâmetros/Services (@Validated) -> 400 Bad Request
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errosCampos = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String caminhoPropriedade = violation.getPropertyPath().toString();
            String nomeCampo = caminhoPropriedade.contains(".")
                    ? caminhoPropriedade.substring(caminhoPropriedade.lastIndexOf('.') + 1)
                    : caminhoPropriedade;
            errosCampos.putIfAbsent(nomeCampo, violation.getMessage());
        });

        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais dados informados são inválidos.",
                request.getRequestURI(),
                errosCampos
        );
        return ResponseEntity.badRequest().body(erro);
    }

    // 8. JSON malformado ou Enum inválido no corpo da requisição -> 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> handleJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "O corpo da requisição está malformado ou contém valores inválidos.",
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(erro);
    }

    // 9. Parâmetro de URL com tipo ou Enum inválido -> 400 Bad Request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> handleParametroInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String mensagem = "O parâmetro '" + ex.getName() + "' recebeu um valor inválido: '" + ex.getValue() + "'.";
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro inválido",
                mensagem,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(erro);
    }

    // 10. Falta de parâmetro ou arquivo obrigatório na requisição -> 400 Bad Request
    @ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ErroResponseDTO> handleParametroOuParteAusente(
            Exception ex,
            HttpServletRequest request
    ) {
        String nomeFaltante = (ex instanceof MissingServletRequestParameterException paramEx)
                ? paramEx.getParameterName()
                : ((MissingServletRequestPartException) ex).getRequestPartName();

        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Dado obrigatório ausente",
                "O campo ou parâmetro obrigatório '" + nomeFaltante + "' não foi enviado.",
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(erro);
    }

    // 11. Arquivo de upload maior que o limite permitido -> 413 Payload Too Large
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroResponseDTO> handleArquivoMuitoGrande(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        ErroResponseDTO erro = new ErroResponseDTO(
                413,
                "Arquivo muito grande",
                "O tamanho do arquivo enviado excede o limite permitido pelo servidor.",
                request.getRequestURI()
        );
        return ResponseEntity.status(413).body(erro);
    }

    // 12. Fallback para qualquer outro erro inesperado -> 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> handleErroGenerico(
            Exception ex,
            HttpServletRequest request
    ) {
        ex.printStackTrace();
        ErroResponseDTO erro = new ErroResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}