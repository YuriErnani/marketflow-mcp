package br.com.marketflow.mcp.estoque.tool;

import br.com.marketflow.mcp.estoque.client.EstoqueClient;
import br.com.marketflow.mcp.estoque.dto.EstoqueResponseDTO;
import br.com.marketflow.mcp.estoque.dto.MovimentacaoEstoqueResponseDTO;
import br.com.marketflow.mcp.infra.dto.PaginaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstoqueTools {

    private final EstoqueClient estoqueClient;

    @McpTool(
            name = "consultar_saldo_produto",
            description = "Consulta o saldo de estoque de um produto do MarketFlow pelo id: a quantidade disponível para venda e a quantidade reservada por pedidos que aguardam pagamento.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public EstoqueResponseDTO consultarSaldoProduto(@McpToolParam(description = "Id do produto") Long produtoId) {
        return estoqueClient.consultarSaldo(produtoId);
    }

    @McpTool(
            name = "historico_estoque",
            description = "Lista as movimentações de estoque de um produto do MarketFlow, da mais recente para a mais antiga (20 por página, começando na página 0). Tipos: ENTRADA (reposição), RESERVA (pedido criado), BAIXA (pagamento aprovado), LIBERACAO (pedido não pago cancelado) e DEVOLUCAO (pedido pago cancelado).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public PaginaResponseDTO<MovimentacaoEstoqueResponseDTO> historicoEstoque(
            @McpToolParam(description = "Id do produto") Long produtoId,
            @McpToolParam(description = "Número da página, começando em 0", required = false) Integer pagina) {

        var numeroPagina = pagina == null ? 0 : pagina;

        return estoqueClient.listarMovimentacoes(produtoId, numeroPagina);
    }

}