package br.com.marketflow.mcp.pedido.client;

import br.com.marketflow.mcp.infra.dto.PaginaResponseDTO;
import br.com.marketflow.mcp.pedido.dto.PedidoResponseDTO;
import br.com.marketflow.mcp.pedido.dto.PedidoResumoResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PedidoClient {

    private final RestClient restClient;

    public PedidoClient(@Qualifier("marketFlowAdminRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public PedidoResponseDTO buscarPorId(Long pedidoId) {
        return restClient
                .get()
                .uri("/pedidos/{id}", pedidoId)
                .retrieve()
                .body(PedidoResponseDTO.class);
    }

    public PaginaResponseDTO<PedidoResumoResponseDTO> listar(int pagina, int tamanho) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pedidos")
                        .queryParam("page", pagina)
                        .queryParam("size", tamanho)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaResponseDTO<PedidoResumoResponseDTO>>() {});
    }

}