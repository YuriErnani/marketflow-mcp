package br.com.marketflow.mcp.produto.dto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        boolean disponivel,
        String categoriaNome
) {}