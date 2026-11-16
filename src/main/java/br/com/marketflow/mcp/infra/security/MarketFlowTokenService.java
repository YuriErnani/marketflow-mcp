package br.com.marketflow.mcp.infra.security;

import br.com.marketflow.mcp.infra.security.dto.LoginRequestDTO;
import br.com.marketflow.mcp.infra.security.dto.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;

@Component
public class MarketFlowTokenService {

    private static final Duration RENOVAR_APOS = Duration.ofMinutes(110);

    private final RestClient restClient;
    private final String email;
    private final String senha;

    private String token;
    private Instant obtidoEm;

    public MarketFlowTokenService(RestClient restClient, @Value("${api.marketflow.email}") String email, @Value("${api.marketflow.senha}") String senha) {
        this.restClient = restClient;
        this.email = email;
        this.senha = senha;
    }

    public synchronized String obterToken() {
        if (token == null || Instant.now().isAfter(obtidoEm.plus(RENOVAR_APOS)))
            renovar();

        return token;
    }

    private void renovar() {
        if (email.isBlank() || senha.isBlank())
            throw new IllegalStateException("Conta de serviço não configurada: defina MARKETFLOW_EMAIL e MARKETFLOW_SENHA.");

        var resposta = restClient
                .post()
                .uri("/auth/login")
                .body(new LoginRequestDTO(email, senha))
                .retrieve()
                .body(TokenResponseDTO.class);

        token = resposta.token();
        obtidoEm = Instant.now();
    }

}