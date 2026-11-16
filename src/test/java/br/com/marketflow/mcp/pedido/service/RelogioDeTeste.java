package br.com.marketflow.mcp.pedido.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

class RelogioDeTeste extends Clock {

    private Instant agora = Instant.parse("2026-01-01T12:00:00Z");

    void avancar(Duration duracao) {
        agora = agora.plus(duracao);
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zona) {
        return this;
    }

    @Override
    public Instant instant() {
        return agora;
    }

}
