package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.penguineering.flaresolverr_rmq_bridge.service.DurationToMillisSerializer;
import com.penguineering.flaresolverr_rmq_bridge.service.DurationToMinutesSerializer;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Data for a Flaresolverr request. If postData is set, a POST request is made, otherwise a GET request is made.
 *
 * @param url
 * @param session
 * @param sessionTtl
 * @param maxTimeout
 * @param cookies
 * @param returnOnlyCookies
 * @param proxy
 * @param postData
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlareSolverrRequest(
        String url,
        UUID session,
        @JsonSerialize(using = DurationToMinutesSerializer.class)
        Duration sessionTtl,
        @JsonSerialize(using = DurationToMillisSerializer.class)
        Duration maxTimeout,
        List<RequestCookie> cookies,
        boolean returnOnlyCookies,
        String proxy,
        String postData
) {
    public record RequestCookie(
            String name,
            String value
    ) {
        /**
         * Create a RequestCookie from a FlareSolverrResponse.Cookie
         *
         * @param cookie the cookie to create the RequestCookie from
         */
        public RequestCookie(FlareSolverrResponse.Cookie cookie) {
            this(cookie.name(), cookie.value());
        }
    }

    public FlareSolverrRequest(String url) {
        this(url, null, null, null, List.of(), false, null, null);
    }

    public FlareSolverrRequest(String url, UUID session) {
        this(url, session, null, null, List.of(), false, null, null);
    }
}
