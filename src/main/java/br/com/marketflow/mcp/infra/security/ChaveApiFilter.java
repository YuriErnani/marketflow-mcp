package br.com.marketflow.mcp.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ChaveApiFilter extends OncePerRequestFilter {

    private static final String CABECALHO = "x-api-key";

    private final byte[] chaveEsperada;

    public ChaveApiFilter(String chave) {
        this.chaveEsperada = chave.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var chaveRecebida = request.getHeader(CABECALHO);

        if (chaveRecebida != null && MessageDigest.isEqual(chaveEsperada, chaveRecebida.getBytes(StandardCharsets.UTF_8))) {
            var autenticacao = UsernamePasswordAuthenticationToken.authenticated("claude-connector", null, AuthorityUtils.NO_AUTHORITIES);
            var contexto = SecurityContextHolder.createEmptyContext();

            contexto.setAuthentication(autenticacao);
            SecurityContextHolder.setContext(contexto);
        }

        filterChain.doFilter(request, response);
    }

}