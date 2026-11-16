package br.com.marketflow.mcp.estoque.tool;

import br.com.marketflow.mcp.estoque.client.EstoqueClient;
import br.com.marketflow.mcp.estoque.dto.EstoqueResponseDTO;
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

}