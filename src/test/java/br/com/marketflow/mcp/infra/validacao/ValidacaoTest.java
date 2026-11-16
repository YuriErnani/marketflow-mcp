package br.com.marketflow.mcp.infra.validacao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidacaoTest {

    @Test
    void deveAceitarIdPositivo() {
        assertThat(Validacao.id(7L, "pedidoId")).isEqualTo(7L);
    }

    @Test
    void deveRecusarIdNegativoOuZero() {
        assertThatThrownBy(() -> Validacao.id(-1L, "pedidoId"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pedidoId deve ser um número positivo.");

        assertThatThrownBy(() -> Validacao.id(0L, "pedidoId"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRecusarIdAusente() {
        assertThatThrownBy(() -> Validacao.id(null, "pedidoId"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pedidoId é obrigatório.");
    }

    @Test
    void deveAceitarIdOpcionalAusente() {
        assertThat(Validacao.idOpcional(null, "categoriaId")).isNull();
    }

    @Test
    void deveUsarPaginaZeroQuandoNaoInformada() {
        assertThat(Validacao.pagina(null)).isZero();
        assertThat(Validacao.pagina(3)).isEqualTo(3);
    }

    @Test
    void deveRecusarPaginaNegativa() {
        assertThatThrownBy(() -> Validacao.pagina(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveUsarPadraoEAceitarLimitesDaFaixa() {
        assertThat(Validacao.entre(null, 5, 1, 1000, "limite")).isEqualTo(5);
        assertThat(Validacao.entre(1, 5, 1, 1000, "limite")).isEqualTo(1);
        assertThat(Validacao.entre(1000, 5, 1, 1000, "limite")).isEqualTo(1000);
    }

    @Test
    void deveRecusarValorForaDaFaixa() {
        assertThatThrownBy(() -> Validacao.entre(0, 5, 1, 1000, "limite"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limite deve estar entre 1 e 1000.");

        assertThatThrownBy(() -> Validacao.entre(1001, 5, 1, 1000, "limite"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveAceitarFaixaDePrecoValidaOuAusente() {
        assertThatCode(() -> Validacao.faixaDePreco(null, null)).doesNotThrowAnyException();
        assertThatCode(() -> Validacao.faixaDePreco(new BigDecimal("100"), new BigDecimal("100"))).doesNotThrowAnyException();
    }

    @Test
    void deveRecusarPrecoMinimoMaiorQueMaximo() {
        assertThatThrownBy(() -> Validacao.faixaDePreco(new BigDecimal("500"), new BigDecimal("100")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("precoMin não pode ser maior que precoMax.");
    }

    @Test
    void deveRecusarPrecoNegativo() {
        assertThatThrownBy(() -> Validacao.faixaDePreco(new BigDecimal("-1"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLimparTextoOpcional() {
        assertThat(Validacao.textoOpcional("  teclado  ", 100, "nome")).isEqualTo("teclado");
        assertThat(Validacao.textoOpcional("   ", 100, "nome")).isNull();
        assertThat(Validacao.textoOpcional(null, 100, "nome")).isNull();
    }

    @Test
    void deveRecusarTextoMuitoLongo() {
        var nomeLongo = "a".repeat(101);

        assertThatThrownBy(() -> Validacao.textoOpcional(nomeLongo, 100, "nome"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveAceitarCodigoDeConfirmacaoComSeisDigitos() {
        assertThat(Validacao.codigoConfirmacao("012345")).isEqualTo("012345");
    }

    @Test
    void deveRecusarCodigoDeConfirmacaoForaDoFormato() {
        assertThatThrownBy(() -> Validacao.codigoConfirmacao("abc"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Validacao.codigoConfirmacao("12345"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Validacao.codigoConfirmacao(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
