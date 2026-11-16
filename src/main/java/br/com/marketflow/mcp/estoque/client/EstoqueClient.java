package br.com.marketflow.mcp.estoque.client;

import br.com.marketflow.mcp.estoque.dto.EstoqueResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EstoqueClient {

    private final RestClient restClient;

    public EstoqueClient(@Qualifier("marketFlowAdminRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public EstoqueResponseDTO consultarSaldo(Long produtoId) {
        return restClient.get()
                .uri("/estoque/{produtoId}", produtoId)
                .retrieve()
                .body(EstoqueResponseDTO.class);
    }

}