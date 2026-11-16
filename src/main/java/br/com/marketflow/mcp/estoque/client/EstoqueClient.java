package br.com.marketflow.mcp.estoque.client;

import br.com.marketflow.mcp.estoque.dto.EstoqueResponseDTO;
import br.com.marketflow.mcp.estoque.dto.MovimentacaoEstoqueResponseDTO;
import br.com.marketflow.mcp.infra.dto.PaginaResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EstoqueClient {

    private final RestClient restClient;

    public EstoqueClient(@Qualifier("marketFlowAdminRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public EstoqueResponseDTO consultarSaldo(Long produtoId) {
        return restClient
                .get()
                .uri("/estoque/{produtoId}", produtoId)
                .retrieve()
                .body(EstoqueResponseDTO.class);
    }

    public PaginaResponseDTO<MovimentacaoEstoqueResponseDTO> listarMovimentacoes(Long produtoId, int pagina) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/estoque/{produtoId}/movimentacoes")
                        .queryParam("page", pagina)
                        .build(produtoId))
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaResponseDTO<MovimentacaoEstoqueResponseDTO>>() {});
    }

}