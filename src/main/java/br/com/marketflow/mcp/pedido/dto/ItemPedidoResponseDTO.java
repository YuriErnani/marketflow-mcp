package br.com.marketflow.mcp.pedido.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long produtoId,
        String nomeProduto,
        BigDecimal precoUnitario,
        int quantidade,
        BigDecimal subtotal
) {}