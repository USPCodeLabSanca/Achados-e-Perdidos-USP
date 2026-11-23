package com.usp.USPerdi.exception;

public class ArmazenamentoArquivoException extends RuntimeException {
    public ArmazenamentoArquivoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}