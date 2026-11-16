package br.com.marketflow.mcp.infra.dto;

import java.util.List;

public record PaginaResponseDTO<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {}