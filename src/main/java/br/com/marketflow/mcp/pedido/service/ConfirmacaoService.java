package br.com.marketflow.mcp.pedido.service;

import br.com.marketflow.mcp.pedido.dto.AcaoPendente;
import br.com.marketflow.mcp.pedido.dto.StatusPedido;
import br.com.marketflow.mcp.pedido.dto.TipoAcaoPedido;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfirmacaoService {

    public static final Duration VALIDADE = Duration.ofMinutes(5);

    private final SecureRandom random = new SecureRandom();
    private final Map<String, AcaoPendente> pendentes = new ConcurrentHashMap<>();

    public String registrar(TipoAcaoPedido acao, Long pedidoId, StatusPedido novoStatus) {
        var agora = Instant.now();

        pendentes.values().removeIf(pendente -> pendente.expiraEm().isBefore(agora));

        var pendente = new AcaoPendente(acao, pedidoId, novoStatus, agora.plus(VALIDADE));
        String codigo;

        do {
            codigo = String.format("%06d", random.nextInt(1_000_000));
        } while (pendentes.putIfAbsent(codigo, pendente) != null);

        return codigo;
    }

    public AcaoPendente consumir(String codigo, TipoAcaoPedido acao, Long pedidoId) {
        var pendente = pendentes.remove(codigo);

        if (pendente == null)
            throw new IllegalArgumentException("Código de confirmação inválido ou já utilizado. Prepare a ação novamente.");

        if (pendente.expiraEm().isBefore(Instant.now()))
            throw new IllegalArgumentException("Código de confirmação expirado. Prepare a ação novamente.");

        if (pendente.acao() != acao || !pendente.pedidoId().equals(pedidoId))
            throw new IllegalArgumentException("Este código de confirmação não corresponde a esta ação neste pedido.");

        return pendente;
    }

}