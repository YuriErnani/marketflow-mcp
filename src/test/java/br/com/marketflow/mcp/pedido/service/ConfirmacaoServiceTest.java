package br.com.marketflow.mcp.pedido.service;

import br.com.marketflow.mcp.pedido.dto.StatusPedido;
import br.com.marketflow.mcp.pedido.dto.TipoAcaoPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfirmacaoServiceTest {

    private RelogioDeTeste relogio;
    private ConfirmacaoService confirmacaoService;

    @BeforeEach
    void preparar() {
        relogio = new RelogioDeTeste();
        confirmacaoService = new ConfirmacaoService(relogio);
    }

    @Test
    void deveGerarCodigoComSeisDigitos() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        assertThat(codigo).matches("\\d{6}");
    }

    @Test
    void deveDevolverAcaoPendenteQuandoCodigoForValido() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.ATUALIZAR_STATUS, 1L, StatusPedido.ENVIADO);

        var pendente = confirmacaoService.consumir(codigo, TipoAcaoPedido.ATUALIZAR_STATUS, 1L);

        assertThat(pendente.acao()).isEqualTo(TipoAcaoPedido.ATUALIZAR_STATUS);
        assertThat(pendente.pedidoId()).isEqualTo(1L);
        assertThat(pendente.novoStatus()).isEqualTo(StatusPedido.ENVIADO);
    }

    @Test
    void deveRecusarCodigoInexistente() {
        assertThatThrownBy(() -> confirmacaoService.consumir("123456", TipoAcaoPedido.CANCELAR, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido ou já utilizado");
    }

    @Test
    void deveRecusarCodigoUsadoDuasVezes() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 1L);

        assertThatThrownBy(() -> confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido ou já utilizado");
    }

    @Test
    void deveAceitarCodigoAntesDeExpirar() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        relogio.avancar(ConfirmacaoService.VALIDADE.minusSeconds(1));

        var pendente = confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 1L);

        assertThat(pendente.pedidoId()).isEqualTo(1L);
    }

    @Test
    void deveRecusarCodigoExpirado() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        relogio.avancar(ConfirmacaoService.VALIDADE.plusSeconds(1));

        assertThatThrownBy(() -> confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void deveRecusarCodigoDeOutroPedido() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        assertThatThrownBy(() -> confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não corresponde");
    }

    @Test
    void deveRecusarCodigoDeOutraAcao() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        assertThatThrownBy(() -> confirmacaoService.consumir(codigo, TipoAcaoPedido.ATUALIZAR_STATUS, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não corresponde");
    }

    @Test
    void deveQueimarCodigoMesmoQuandoForUsadoNoPedidoErrado() {
        var codigo = confirmacaoService.registrar(TipoAcaoPedido.CANCELAR, 1L, null);

        assertThatThrownBy(() -> confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 2L))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> confirmacaoService.consumir(codigo, TipoAcaoPedido.CANCELAR, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido ou já utilizado");
    }

}
