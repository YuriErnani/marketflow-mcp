package br.com.marketflow.mcp.pedido.service;

import br.com.marketflow.mcp.pedido.client.PedidoClient;
import br.com.marketflow.mcp.pedido.dto.PedidoResponseDTO;
import br.com.marketflow.mcp.pedido.dto.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoClient pedidoClient;

    private PedidoService pedidoService;

    @BeforeEach
    void preparar() {
        pedidoService = new PedidoService(pedidoClient, new ConfirmacaoService());
    }

    private PedidoResponseDTO pedido(Long id, StatusPedido status) {
        return new PedidoResponseDTO(id, 10L, status, new BigDecimal("100.00"), Instant.parse("2026-01-01T12:00:00Z"), List.of());
    }

    @Test
    void devePrepararCancelamentoDePedidoNaoPagoSemEstorno() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.AGUARDANDO_PAGAMENTO));

        var preparacao = pedidoService.prepararCancelamento(1L);

        assertThat(preparacao.statusAtual()).isEqualTo(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(preparacao.efeitos()).contains("A reserva de estoque dos itens é liberada.");
        assertThat(preparacao.efeitos()).doesNotContain("O pagamento aprovado é estornado.");
        assertThat(preparacao.codigoConfirmacao()).matches("\\d{6}");
        verify(pedidoClient, never()).cancelar(any());
    }

    @Test
    void devePrepararCancelamentoDePedidoPagoComEstorno() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.PAGAMENTO_APROVADO));

        var preparacao = pedidoService.prepararCancelamento(1L);

        assertThat(preparacao.efeitos()).contains("Os itens voltam para o estoque disponível.", "O pagamento aprovado é estornado.");
    }

    @Test
    void deveRecusarCancelamentoDePedidoEntregue() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.ENTREGUE));

        assertThatThrownBy(() -> pedidoService.prepararCancelamento(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ENTREGUE");
    }

    @Test
    void deveCancelarQuandoCodigoForValido() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.AGUARDANDO_PAGAMENTO));
        when(pedidoClient.cancelar(1L)).thenReturn(pedido(1L, StatusPedido.CANCELADO));

        var codigo = pedidoService.prepararCancelamento(1L).codigoConfirmacao();
        var pedidoCancelado = pedidoService.confirmarCancelamento(1L, codigo);

        assertThat(pedidoCancelado.status()).isEqualTo(StatusPedido.CANCELADO);
        verify(pedidoClient, times(1)).cancelar(1L);
    }

    @Test
    void naoDeveCancelarOutroPedidoComOCodigoRecebido() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.AGUARDANDO_PAGAMENTO));

        var codigo = pedidoService.prepararCancelamento(1L).codigoConfirmacao();

        assertThatThrownBy(() -> pedidoService.confirmarCancelamento(2L, codigo))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoClient, never()).cancelar(any());
    }

    @Test
    void deveRecusarPularEtapaDeStatus() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.PAGAMENTO_APROVADO));

        assertThatThrownBy(() -> pedidoService.prepararAtualizacaoStatus(1L, StatusPedido.ENTREGUE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EM_PREPARACAO");
    }

    @Test
    void deveAtualizarStatusQuandoCodigoForValido() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.EM_PREPARACAO));
        when(pedidoClient.atualizarStatus(1L, StatusPedido.ENVIADO)).thenReturn(pedido(1L, StatusPedido.ENVIADO));

        var codigo = pedidoService.prepararAtualizacaoStatus(1L, StatusPedido.ENVIADO).codigoConfirmacao();
        var pedidoAtualizado = pedidoService.confirmarAtualizacaoStatus(1L, StatusPedido.ENVIADO, codigo);

        assertThat(pedidoAtualizado.status()).isEqualTo(StatusPedido.ENVIADO);
        verify(pedidoClient, times(1)).atualizarStatus(1L, StatusPedido.ENVIADO);
    }

    @Test
    void naoDeveAtualizarParaStatusDiferenteDoPreparado() {
        when(pedidoClient.buscarPorId(1L)).thenReturn(pedido(1L, StatusPedido.EM_PREPARACAO));

        var codigo = pedidoService.prepararAtualizacaoStatus(1L, StatusPedido.ENVIADO).codigoConfirmacao();

        assertThatThrownBy(() -> pedidoService.confirmarAtualizacaoStatus(1L, StatusPedido.ENTREGUE, codigo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ENVIADO");

        verify(pedidoClient, never()).atualizarStatus(any(), any());
    }

}
