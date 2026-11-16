package br.com.marketflow.mcp.estoque.service;

import br.com.marketflow.mcp.estoque.client.EstoqueClient;
import br.com.marketflow.mcp.estoque.dto.EstoqueBaixoResponseDTO;
import br.com.marketflow.mcp.estoque.dto.ListaEstoqueBaixoResponseDTO;
import br.com.marketflow.mcp.produto.client.ProdutoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private static final int TAMANHO_PAGINA = 50;
    private static final int MAXIMO_PAGINAS = 2;

    private final ProdutoClient produtoClient;
    private final EstoqueClient estoqueClient;

    public ListaEstoqueBaixoResponseDTO listarEstoqueBaixo(int limite) {
        var produtos = new ArrayList<EstoqueBaixoResponseDTO>();
        var produtosAnalisados = 0;
        var totalPaginas = 0;

        for (var pagina = 0; pagina < MAXIMO_PAGINAS; pagina++) {
            var resposta = produtoClient.listar(pagina, TAMANHO_PAGINA);

            produtosAnalisados += resposta.conteudo().size();
            totalPaginas = resposta.totalPaginas();

            for (var produto : resposta.conteudo()) {
                var saldo = estoqueClient.consultarSaldo(produto.id());

                if (saldo.quantidadeDisponivel() < limite)
                    produtos.add(new EstoqueBaixoResponseDTO(produto.id(), produto.nome(), saldo.quantidadeDisponivel(), saldo.quantidadeReservada()));
            }

            if (pagina + 1 >= totalPaginas)
                break;
        }

        produtos.sort(Comparator.comparingInt(EstoqueBaixoResponseDTO::quantidadeDisponivel));

        return new ListaEstoqueBaixoResponseDTO(produtos, produtosAnalisados, totalPaginas > MAXIMO_PAGINAS);
    }

}