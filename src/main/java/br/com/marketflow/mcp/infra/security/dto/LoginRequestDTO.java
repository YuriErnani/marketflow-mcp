package br.com.marketflow.mcp.infra.security.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {}