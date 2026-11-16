package br.com.marketflow.mcp.estoque.dto;

public record EstoqueBaixoResponseDTO(
        Long produtoId,
        String nome,
        int quantidadeDisponivel,
        int quantidadeReservada
) {}