package br.com.marketflow.mcp.pedido.dto;

import java.time.Instant;

public record AcaoPendente(
        TipoAcaoPedido acao,
        Long pedidoId,
        StatusPedido novoStatus,
        Instant expiraEm
) {}