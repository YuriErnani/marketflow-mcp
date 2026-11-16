package br.com.marketflow.mcp.estoque.dto;

import java.util.List;

public record ListaEstoqueBaixoResponseDTO(
        List<EstoqueBaixoResponseDTO> produtos,
        int produtosAnalisados,
        boolean listaIncompleta
) {}