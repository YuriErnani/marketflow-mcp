package br.com.marketflow.mcp.pedido.tool;

import br.com.marketflow.mcp.infra.auditoria.AuditoriaService;
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
    private final AuditoriaService auditoriaService;

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
        return auditoriaService.auditar("preparar_cancelamento", "pedidoId=" + pedidoId, () -> pedidoService.prepararCancelamento(pedidoId));
    }

    @McpTool(
            name = "confirmar_cancelamento",
            description = "Segunda etapa: cancela de fato um pedido do MarketFlow, usando o código devolvido por preparar_cancelamento para o mesmo pedido. Só use depois que o usuário concordar com os efeitos. Cada código vale uma única vez.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = true, idempotentHint = false)
    )
    public PedidoResponseDTO confirmarCancelamento(
            @McpToolParam(description = "Id do pedido") Long pedidoId,
            @McpToolParam(description = "Código de confirmação devolvido por preparar_cancelamento") String codigoConfirmacao) {

        return auditoriaService.auditar("confirmar_cancelamento", "pedidoId=" + pedidoId, () -> pedidoService.confirmarCancelamento(pedidoId, codigoConfirmacao));
    }

    @McpTool(
            name = "preparar_atualizacao_status",
            description = "Primeira etapa da mudança de status de um pedido do MarketFlow. NÃO altera nada: verifica se a mudança é permitida (o status só avança um passo: PAGAMENTO_APROVADO → EM_PREPARACAO → ENVIADO → ENTREGUE), descreve os efeitos e devolve um código de confirmação válido por 5 minutos. Mostre os efeitos ao usuário e só chame confirmar_atualizacao_status se ele concordar.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public PreparacaoAcaoResponseDTO prepararAtualizacaoStatus(
            @McpToolParam(description = "Id do pedido") Long pedidoId,
            @McpToolParam(description = "Novo status do pedido") StatusPedido novoStatus) {

        return auditoriaService.auditar("preparar_atualizacao_status", "pedidoId=" + pedidoId + " novoStatus=" + novoStatus, () -> pedidoService.prepararAtualizacaoStatus(pedidoId, novoStatus));
    }

    @McpTool(
            name = "confirmar_atualizacao_status",
            description = "Segunda etapa: altera de fato o status de um pedido do MarketFlow, usando o código devolvido por preparar_atualizacao_status para o mesmo pedido e o mesmo novo status. Só use depois que o usuário concordar com os efeitos. Cada código vale uma única vez.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = true, idempotentHint = false)
    )
    public PedidoResponseDTO confirmarAtualizacaoStatus(
            @McpToolParam(description = "Id do pedido") Long pedidoId,
            @McpToolParam(description = "Novo status do pedido, o mesmo usado na preparação") StatusPedido novoStatus,
            @McpToolParam(description = "Código de confirmação devolvido por preparar_atualizacao_status") String codigoConfirmacao) {

        return auditoriaService.auditar("confirmar_atualizacao_status", "pedidoId=" + pedidoId + " novoStatus=" + novoStatus, () -> pedidoService.confirmarAtualizacaoStatus(pedidoId, novoStatus, codigoConfirmacao));
    }

}