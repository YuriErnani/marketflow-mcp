package br.com.marketflow.mcp.estoque.dto;

public record EstoqueResponseDTO(
        Long produtoId,
        int quantidadeDisponivel,
        int quantidadeReservada
) {}