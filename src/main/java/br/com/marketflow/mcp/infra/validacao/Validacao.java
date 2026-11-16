package br.com.marketflow.mcp.infra.validacao;

import java.math.BigDecimal;

public final class Validacao {

    private Validacao() {
    }

    public static <T> T obrigatorio(T valor, String campo) {
        if (valor == null)
            throw new IllegalArgumentException(campo + " é obrigatório.");

        return valor;
    }

    public static Long id(Long valor, String campo) {
        obrigatorio(valor, campo);

        if (valor <= 0)
            throw new IllegalArgumentException(campo + " deve ser um número positivo.");

        return valor;
    }

    public static Long idOpcional(Long valor, String campo) {
        return valor == null ? null : id(valor, campo);
    }

    public static int pagina(Integer pagina) {
        if (pagina == null)
            return 0;

        if (pagina < 0)
            throw new IllegalArgumentException("pagina não pode ser negativa. A primeira página é a 0.");

        return pagina;
    }

    public static int entre(Integer valor, int padrao, int minimo, int maximo, String campo) {
        if (valor == null)
            return padrao;

        if (valor < minimo || valor > maximo)
            throw new IllegalArgumentException(campo + " deve estar entre " + minimo + " e " + maximo + ".");

        return valor;
    }

    public static void faixaDePreco(BigDecimal precoMin, BigDecimal precoMax) {
        if (precoMin != null && precoMin.signum() < 0)
            throw new IllegalArgumentException("precoMin não pode ser negativo.");

        if (precoMax != null && precoMax.signum() < 0)
            throw new IllegalArgumentException("precoMax não pode ser negativo.");

        if (precoMin != null && precoMax != null && precoMin.compareTo(precoMax) > 0)
            throw new IllegalArgumentException("precoMin não pode ser maior que precoMax.");
    }

    public static String textoOpcional(String valor, int tamanhoMaximo, String campo) {
        if (valor == null || valor.isBlank())
            return null;

        var texto = valor.strip();

        if (texto.length() > tamanhoMaximo)
            throw new IllegalArgumentException(campo + " pode ter no máximo " + tamanhoMaximo + " caracteres.");

        return texto;
    }

    public static String codigoConfirmacao(String codigo) {
        obrigatorio(codigo, "codigoConfirmacao");

        if (!codigo.matches("\\d{6}"))
            throw new IllegalArgumentException("codigoConfirmacao deve ter exatamente 6 dígitos.");

        return codigo;
    }

}