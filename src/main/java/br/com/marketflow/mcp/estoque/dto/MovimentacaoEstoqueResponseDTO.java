package br.com.marketflow.mcp.estoque.dto;

import java.time.Instant;

public record MovimentacaoEstoqueResponseDTO(
        Long id,
        TipoMovimentacao tipo,
        int quantidade,
        Long pedidoId,
        Instant criadoEm
) {}