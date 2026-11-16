package br.com.marketflow.mcp.infra.exception;

public class MarketFlowException extends RuntimeException {

    public MarketFlowException(String mensagem) {
        super(mensagem);
    }

}