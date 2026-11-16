package br.com.marketflow.mcp.pedido.dto;

import java.util.List;

public record ListaPedidosResponseDTO(
        List<PedidoResumoResponseDTO> pedidos,
        int pedidosAnalisados,
        boolean listaIncompleta
) {}