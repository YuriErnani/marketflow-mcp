package br.com.marketflow.mcp.pedido.service;

import br.com.marketflow.mcp.pedido.client.PedidoClient;
import br.com.marketflow.mcp.pedido.dto.ListaPedidosResponseDTO;
import br.com.marketflow.mcp.pedido.dto.PedidoResumoResponseDTO;
import br.com.marketflow.mcp.pedido.dto.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final int TAMANHO_PAGINA = 50;
    private static final int MAXIMO_PAGINAS = 4;

    private final PedidoClient pedidoClient;

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

}