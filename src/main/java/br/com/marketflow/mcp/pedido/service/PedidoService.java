package br.com.marketflow.mcp.pedido.service;

import br.com.marketflow.mcp.pedido.client.PedidoClient;
import br.com.marketflow.mcp.pedido.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final int TAMANHO_PAGINA = 50;
    private static final int MAXIMO_PAGINAS = 4;

    private final PedidoClient pedidoClient;
    private final ConfirmacaoService confirmacaoService;

    public ListaPedidosResponseDTO listarPedidos(StatusPedido status) {
        var pedidos = new ArrayList<PedidoResumoResponseDTO>();
        var pedidosAnalisados = 0;
        var totalPaginas = 0;

        for (var pagina = 0; pagina < MAXIMO_PAGINAS; pagina++) {
            var resposta = pedidoClient.listar(pagina, TAMANHO_PAGINA);

            pedidosAnalisados += resposta.conteudo().size();
            totalPaginas = resposta.totalPaginas();

            resposta.conteudo()
                    .stream()
                    .filter(pedido -> status == null || pedido.status() == status)
                    .forEach(pedidos::add);

            if (pagina + 1 >= totalPaginas)
                break;
        }

        return new ListaPedidosResponseDTO(pedidos, pedidosAnalisados, totalPaginas > MAXIMO_PAGINAS);
    }

    public PreparacaoAcaoResponseDTO prepararCancelamento(Long pedidoId) {
        var pedido = pedidoClient.buscarPorId(pedidoId);

        var efeitos = switch (pedido.status()) {
            case AGUARDANDO_PAGAMENTO -> List.of(
                    "O pedido passa para CANCELADO.",
                    "A reserva de estoque dos itens é liberada.",
                    "Nenhum pagamento é estornado, porque o pedido ainda não foi pago.");
            case PAGAMENTO_APROVADO, EM_PREPARACAO -> List.of(
                    "O pedido passa para CANCELADO.",
                    "Os itens voltam para o estoque disponível.",
                    "O pagamento aprovado é estornado.");
            default -> throw new IllegalStateException("Pedidos com status " + pedido.status() + " não podem ser cancelados.");
        };

        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, pedidoId, null);

        return new PreparacaoAcaoResponseDTO(pedidoId, pedido.status(), pedido.total(), efeitos, codigo, ConfirmacaoService.VALIDADE.toMinutes());
    }

    public PedidoResponseDTO confirmarCancelamento(Long pedidoId, String codigoConfirmacao) {
        confirmacaoService.consumir(codigoConfirmacao, TipoAcaoPedido.CANCELAR, pedidoId);

        return pedidoClient.cancelar(pedidoId);
    }

    public PreparacaoAcaoResponseDTO prepararAtualizacaoStatus(Long pedidoId, StatusPedido novoStatus) {
        var pedido = pedidoClient.buscarPorId(pedidoId);

        var proximoStatus = switch (pedido.status()) {
            case PAGAMENTO_APROVADO -> StatusPedido.EM_PREPARACAO;
            case EM_PREPARACAO -> StatusPedido.ENVIADO;
            case ENVIADO -> StatusPedido.ENTREGUE;
            default -> null;
        };

        if (proximoStatus == null)
            throw new IllegalStateException("O status de pedidos " + pedido.status() + " não pode ser alterado por esta ferramenta.");

        if (novoStatus != proximoStatus)
            throw new IllegalStateException("O pedido está " + pedido.status() + ": o único próximo status possível é " + proximoStatus + ".");

        var efeitos = List.of(
                "O status do pedido passa de " + pedido.status() + " para " + novoStatus + ".",
                "Não é possível voltar ao status anterior.",
                "O estoque e o pagamento não são alterados.");

        var codigo = confirmacaoService.registrar(TipoAcaoPedido.ATUALIZAR_STATUS, pedidoId, novoStatus);

        return new PreparacaoAcaoResponseDTO(pedidoId, pedido.status(), pedido.total(), efeitos, codigo, ConfirmacaoService.VALIDADE.toMinutes());
    }

    public PedidoResponseDTO confirmarAtualizacaoStatus(Long pedidoId, StatusPedido novoStatus, String codigoConfirmacao) {
        var pendente = confirmacaoService.consumir(codigoConfirmacao, TipoAcaoPedido.ATUALIZAR_STATUS, pedidoId);

        if (pendente.novoStatus() != novoStatus)
            throw new IllegalArgumentException("Este código foi gerado para o status " + pendente.novoStatus() + ", e não para " + novoStatus + ".");

        return pedidoClient.atualizarStatus(pedidoId, novoStatus);
    }

}