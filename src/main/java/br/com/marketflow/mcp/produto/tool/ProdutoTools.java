package br.com.marketflow.mcp.produto.tool;

import br.com.marketflow.mcp.infra.dto.PaginaResponseDTO;
import br.com.marketflow.mcp.infra.exception.TradutorErros;
import br.com.marketflow.mcp.infra.validacao.Validacao;
import br.com.marketflow.mcp.produto.client.ProdutoClient;
import br.com.marketflow.mcp.produto.dto.ProdutoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProdutoTools {

    private final ProdutoClient produtoClient;
    private final TradutorErros tradutorErros;

    @McpTool(
            name = "buscar_produtos",
            description = "Busca produtos no catálogo do MarketFlow, com filtros opcionais. O resultado é paginado (10 por página, começando na página 0) e informa o total de páginas: se houver mais, chame de novo com a próxima página.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public PaginaResponseDTO<ProdutoResponseDTO> buscarProdutos(
            @McpToolParam(description = "Trecho do nome do produto, sem diferenciar maiúsculas e minúsculas (até 100 caracteres)", required = false) String nome,
            @McpToolParam(description = "Id da categoria", required = false) Long categoriaId,
            @McpToolParam(description = "Preço mínimo, em reais", required = false) BigDecimal precoMin,
            @McpToolParam(description = "Preço máximo, em reais", required = false) BigDecimal precoMax,
            @McpToolParam(description = "Número da página, começando em 0", required = false) Integer pagina) {

        var nomeBusca = Validacao.textoOpcional(nome, 100, "nome");
        var idCategoria = Validacao.idOpcional(categoriaId, "categoriaId");
        Validacao.faixaDePreco(precoMin, precoMax);
        var numeroPagina = Validacao.pagina(pagina);

        return tradutorErros.executar(() -> produtoClient.buscar(nomeBusca, idCategoria, precoMin, precoMax, numeroPagina));
    }

}