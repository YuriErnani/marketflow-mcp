package br.com.marketflow.mcp.infra.auditoria;

import br.com.marketflow.mcp.infra.exception.TradutorErros;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j(topic = "AUDITORIA")
@Component
@RequiredArgsConstructor
public class AuditoriaService {

    private final TradutorErros tradutorErros;

    public <T> T auditar(String ferramenta, String parametros, Supplier<T> acao) {
        try {
            var resultado = tradutorErros.executar(acao);

            log.info("ferramenta={} parametros=[{}] resultado=SUCESSO", ferramenta, parametros);

            return resultado;
        } catch (RuntimeException e) {
            log.warn("ferramenta={} parametros=[{}] resultado=FALHA motivo=\"{}\"", ferramenta, parametros, e.getMessage());

            throw e;
        }
    }

}