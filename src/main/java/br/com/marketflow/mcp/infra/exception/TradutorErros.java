package br.com.marketflow.mcp.infra.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

@Component
public class TradutorErros {

    private final String url;

    public TradutorErros(@Value("${api.marketflow.url}") String url) {
        this.url = url;
    }

    public <T> T executar(Supplier<T> acao) {
        try {
            return acao.get();
        } catch (RestClientResponseException e) {
            throw new MarketFlowException(traduzir(e));
        } catch (ResourceAccessException e) {
            throw new MarketFlowException("Não foi possível falar com o MarketFlow em " + url + ". Verifique se ele está rodando e tente novamente.");
        }
    }

    private String traduzir(RestClientResponseException e) {
        var status = e.getStatusCode().value();

        if (status == 401)
            return "A conta de serviço não conseguiu se autenticar no MarketFlow. Verifique MARKETFLOW_EMAIL e MARKETFLOW_SENHA.";

        if (status == 403)
            return "A conta de serviço não tem permissão para esta operação no MarketFlow.";

        if (e.getStatusCode().is5xxServerError())
            return "O MarketFlow teve um erro interno (HTTP " + status + "). Tente novamente mais tarde.";

        var detalhe = detalheDoProblema(e);

        return detalhe != null ? detalhe : "O MarketFlow recusou a operação (HTTP " + status + ").";
    }

    private String detalheDoProblema(RestClientResponseException e) {
        try {
            var problema = e.getResponseBodyAs(ProblemDetail.class);

            return problema != null ? problema.getDetail() : null;
        } catch (RuntimeException ignorada) {
            return null;
        }
    }

}