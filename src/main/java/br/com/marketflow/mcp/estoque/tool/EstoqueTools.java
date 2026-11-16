package br.com.marketflow.mcp.estoque.tool;

import br.com.marketflow.mcp.estoque.client.EstoqueClient;
import br.com.marketflow.mcp.estoque.dto.EstoqueResponseDTO;
import br.com.marketflow.mcp.estoque.dto.ListaEstoqueBaixoResponseDTO;
import br.com.marketflow.mcp.estoque.dto.MovimentacaoEstoqueResponseDTO;
import br.com.marketflow.mcp.estoque.service.EstoqueService;
import br.com.marketflow.mcp.infra.dto.PaginaResponseDTO;
import br.com.marketflow.mcp.infra.exception.TradutorErros;
import br.com.marketflow.mcp.infra.validacao.Validacao;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstoqueTools {

    private final EstoqueClient estoqueClient;
    private final EstoqueService estoqueService;
    private final TradutorErros tradutorErros;

    @McpTool(
            name = "consultar_saldo_produto",
            description = "Consulta o saldo de estoque de um produto do MarketFlow pelo id: a quantidade disponível para venda e a quantidade reservada por pedidos que aguardam pagamento.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public EstoqueResponseDTO consultarSaldoProduto(@McpToolParam(description = "Id do produto") Long produtoId) {
        var id = Validacao.id(produtoId, "produtoId");

        return tradutorErros.executar(() -> estoqueClient.consultarSaldo(id));
    }

    @McpTool(
            name = "historico_estoque",
            description = "Lista as movimentações de estoque de um produto do MarketFlow, da mais recente para a mais antiga (20 por página, começando na página 0). Tipos: ENTRADA (reposição), RESERVA (pedido criado), BAIXA (pagamento aprovado), LIBERACAO (pedido não pago cancelado) e DEVOLUCAO (pedido pago cancelado).",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public PaginaResponseDTO<MovimentacaoEstoqueResponseDTO> historicoEstoque(
            @McpToolParam(description = "Id do produto") Long produtoId,
            @McpToolParam(description = "Número da página, começando em 0", required = false) Integer pagina) {

        var id = Validacao.id(produtoId, "produtoId");
        var numeroPagina = Validacao.pagina(pagina);

        return tradutorErros.executar(() -> estoqueClient.listarMovimentacoes(id, numeroPagina));
    }

    @McpTool(
            name = "listar_estoque_baixo",
            description = "Lista os produtos ativos do MarketFlow com saldo disponível menor que o limite informado, do menor saldo para o maior. Analisa no máximo 100 produtos, consultando o saldo de cada um: se listaIncompleta for true, existem produtos que não foram analisados.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public ListaEstoqueBaixoResponseDTO listarEstoqueBaixo(
            @McpToolParam(description = "Limite de saldo disponível, entre 1 e 1000: entram os produtos com saldo menor que este número. Padrão: 5.", required = false) Integer limite) {

        var limiteSaldo = Validacao.entre(limite, 5, 1, 1000, "limite");

        return tradutorErros.executar(() -> estoqueService.listarEstoqueBaixo(limiteSaldo));
    }

}