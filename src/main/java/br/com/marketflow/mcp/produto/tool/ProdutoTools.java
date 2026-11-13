package br.com.marketflow.mcp.produto.tool;

import br.com.marketflow.mcp.produto.dto.ProdutoResponseDTO;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProdutoTools {

    private static final List<ProdutoResponseDTO> PRODUTOS_FICTICIOS = List.of(
            new ProdutoResponseDTO(1L, "Teclado Mecânico", new BigDecimal("349.90"), true, "Periféricos"),
            new ProdutoResponseDTO(2L, "Mouse Sem Fio", new BigDecimal("129.90"), true, "Periféricos"),
            new ProdutoResponseDTO(3L, "Monitor 27 Polegadas", new BigDecimal("1499.00"), false, "Monitores")
    );

    @McpTool(
            name = "buscar_produtos",
            description = "Busca produtos do catálogo do MarketFlow pelo nome. Sem nome, lista todos.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true)
    )
    public List<ProdutoResponseDTO> buscarProdutos(@McpToolParam(description = "Trecho do nome do produto, sem diferenciar maiúsculas e minúsculas", required = false) String nome) {
        if (nome == null || nome.isBlank())
            return PRODUTOS_FICTICIOS;

        var termo = nome.toLowerCase();

        return PRODUTOS_FICTICIOS
                .stream()
                .filter(produto -> produto.nome().toLowerCase().contains(termo))
                .toList();
    }

}