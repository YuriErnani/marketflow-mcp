package br.com.marketflow.mcp.pedido.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoResumoResponseDTO(
        Long id,
        Long usuarioId,
        StatusPedido status,
        BigDecimal total,
        Instant criadoEm
) {}