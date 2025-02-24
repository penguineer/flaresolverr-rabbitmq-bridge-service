package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FlareSolverrResponse(
        Solution solution,
        String status,
        String message,
        Instant startTimestamp,
        Instant endTimestamp,
        String version
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Solution(
            String url,
            int status,
            Map<String, String> headers,
            String response,
            List<Cookie> cookies,
            String userAgent
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cookie(
            String name,
            String value,
            String domain,
            String path,
            Instant expiry,
            int size,
            boolean httpOnly,
            boolean secure,
            boolean session,
            String sameSite
    ) {
    }
}
