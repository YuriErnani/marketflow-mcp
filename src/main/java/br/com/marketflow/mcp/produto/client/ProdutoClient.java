package br.com.marketflow.mcp.produto.client;

import br.com.marketflow.mcp.infra.dto.PaginaResponseDTO;
import br.com.marketflow.mcp.produto.dto.ProdutoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProdutoClient {

    private final RestClient restClient;

    public PaginaResponseDTO<ProdutoResponseDTO> buscar(String nome, Long categoriaId, BigDecimal precoMin, BigDecimal precoMax, int pagina) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/produtos")
                        .queryParamIfPresent("nome", Optional.ofNullable(nome))
                        .queryParamIfPresent("categoriaId", Optional.ofNullable(categoriaId))
                        .queryParamIfPresent("precoMin", Optional.ofNullable(precoMin))
                        .queryParamIfPresent("precoMax", Optional.ofNullable(precoMax))
                        .queryParam("page", pagina)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<PaginaResponseDTO<ProdutoResponseDTO>>() {});
    }

}