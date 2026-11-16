package br.com.marketflow.mcp.infra.security.dto;

public record TokenResponseDTO(
        String token,
        String tipo
) {}