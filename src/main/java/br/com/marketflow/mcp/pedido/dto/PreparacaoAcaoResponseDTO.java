package br.com.marketflow.mcp.pedido.dto;

import java.math.BigDecimal;
import java.util.List;

public record PreparacaoAcaoResponseDTO(
        Long pedidoId,
        StatusPedido statusAtual,
        BigDecimal total,
        List<String> efeitos,
        String codigoConfirmacao,
        long validadeMinutos
) {}