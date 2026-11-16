package br.com.marketflow.mcp.pedido.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long usuarioId,
        StatusPedido status,
        BigDecimal total,
        Instant criadoEm,
        List<ItemPedidoResponseDTO> itens
) {}