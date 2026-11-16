package br.com.marketflow.mcp.pedido.tool;

import br.com.marketflow.mcp.pedido.client.PedidoClient;
import br.com.marketflow.mcp.pedido.dto.ListaPedidosResponseDTO;
import br.com.marketflow.mcp.pedido.dto.PedidoResponseDTO;
import br.com.marketflow.mcp.pedido.dto.PreparacaoAcaoResponseDTO;
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

    @McpTool(
            name = "preparar_cancelamento",
            description = "Primeira etapa do cancelamento de um pedido do MarketFlow. NÃO cancela nada: verifica se o pedido pode ser cancelado, descreve os efeitos e devolve um código de confirmação válido por 5 minutos. Mostre os efeitos ao usuário e só chame confirmar_cancelamento se ele concordar.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public PreparacaoAcaoResponseDTO prepararCancelamento(@McpToolParam(description = "Id do pedido") Long pedidoId) {
        return pedidoService.prepararCancelamento(pedidoId);
    }

    @McpTool(
            name = "confirmar_cancelamento",
            description = "Segunda etapa: cancela de fato um pedido do MarketFlow, usando o código devolvido por preparar_cancelamento para o mesmo pedido. Só use depois que o usuário concordar com os efeitos. Cada código vale uma única vez.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = true, idempotentHint = false)
    )
    public PedidoResponseDTO confirmarCancelamento(
            @McpToolParam(description = "Id do pedido") Long pedidoId,
            @McpToolParam(description = "Código de confirmação devolvido por preparar_cancelamento") String codigoConfirmacao) {

        return pedidoService.confirmarCancelamento(pedidoId, codigoConfirmacao);
    }

}