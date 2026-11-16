package br.com.marketflow.mcp.pedido.tool;

import br.com.marketflow.mcp.pedido.client.PedidoClient;
import br.com.marketflow.mcp.pedido.dto.ListaPedidosResponseDTO;
import br.com.marketflow.mcp.pedido.dto.PedidoResponseDTO;
import br.com.marketflow.mcp.pedido.dto.StatusPedido;
import br.com.marketflow.mcp.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoTools {

    private final PedidoClient pedidoClient;
    private final PedidoService pedidoService;

    @McpTool(
            name = "consultar_pedido",
            description = "Consulta um pedido do MarketFlow pelo id, com status, total, data de criação e itens.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public PedidoResponseDTO consultarPedido(@McpToolParam(description = "Id do pedido") Long pedidoId) {
        return pedidoClient.buscarPorId(pedidoId);
    }

    @McpTool(
            name = "listar_pedidos",
            description = "Lista os pedidos do MarketFlow, do mais recente para o mais antigo, com filtro opcional por status. Analisa no máximo os 200 pedidos mais recentes: se listaIncompleta for true, existem pedidos mais antigos que não foram analisados.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public ListaPedidosResponseDTO listarPedidos(
            @McpToolParam(description = "Status para filtrar. Sem status, lista todos.", required = false) StatusPedido status) {

        return pedidoService.listarPedidos(status);
    }

}