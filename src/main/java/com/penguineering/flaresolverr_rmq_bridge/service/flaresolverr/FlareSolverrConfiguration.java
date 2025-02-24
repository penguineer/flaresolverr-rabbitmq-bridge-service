package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public record FlareSolverrConfiguration(
        Duration session_ttl,
        Duration maxTimeout,
        URI proxy) {

    public FlareSolverrConfiguration {
        session_ttl = Objects.requireNonNullElse(session_ttl, Duration.ofMinutes(10));
        maxTimeout = Objects.requireNonNullElse(maxTimeout, Duration.ofMillis(60_000));
    }

    public FlareSolverrConfiguration() {
        this(null, null, null);
    }

    public FlareSolverrConfiguration(String proxy) {
        this(null, null, URI.create(proxy));
    }
}
