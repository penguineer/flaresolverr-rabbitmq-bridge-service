package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FlareSolverrBackend {
    private final URI serviceURI;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public FlareSolverrBackend(@Value("${flaresolverr-rmq-bridge.service.flaresolverr.url}") URI serviceURI,
                               ObjectMapper objectMapper) {
        this.serviceURI = serviceURI;
        this.objectMapper = objectMapper;
    }

    private <T> CompletableFuture<HttpResponse<T>> asyncRequestFromBackend(String json, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(serviceURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return httpClient.sendAsync(httpRequest, bodyHandler);
    }

    private Mono<HttpResponse<String>> reactiveRequestFromBackend(String json) {
        var asyncResponse = asyncRequestFromBackend(json, HttpResponse.BodyHandlers.ofString());

        return Mono.fromFuture(asyncResponse)
                .onErrorMap(IOException.class, FlareSolverrException::new);
    }

    private <T> Mono<T> decodeJson(String json, Class<T> clazz) {
        return Mono
                .justOrEmpty(json)
                .handle((body, sink) -> {
                    try {
                        sink.next(objectMapper.readValue(body, clazz));
                    } catch (JsonProcessingException e) {
                        sink.error(new FlareSolverrException(e));
                    }
                });
    }

    public Mono<FlareSolverrResponse> request(FlareSolverrRequest request) {
        try {
            Map<String, Object> requestData = objectMapper.convertValue(request, Map.class);

            requestData.put("cmd",
                    request.postData() != null ? "request.post" : "request.get");

            // Convert the Map back to JSON string
            String json = objectMapper.writeValueAsString(requestData);

            // Make the request to the serviceUrl
            return reactiveRequestFromBackend(json)
                    .map(HttpResponse::body)
                    .flatMap(body -> decodeJson(body, FlareSolverrResponse.class));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
            // Handle the exception appropriately
        }
    }

    public Mono<UUID> createSession(@Nullable UUID session) {
        var sessionId = Objects.requireNonNullElse(session, UUID.randomUUID());

        // TODO proxy parameter
        Map<String, String> requestData = Map.of(
                "cmd", "sessions.create",
                "session", sessionId.toString()
        );

        try {
            String json = objectMapper.writeValueAsString(requestData);

            // Make the request to the serviceUrl
            return reactiveRequestFromBackend(json)
                    .thenReturn(sessionId);

            //TODO process response

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<Void> destroySession(UUID session) {
        Map<String, String> requestData = Map.of(
                "cmd", "sessions.destroy",
                "session", session.toString()
        );

        try {
            String json = objectMapper.writeValueAsString(requestData);

            // Make the request to the serviceUrl
            return reactiveRequestFromBackend(json)
                    .then();

            // TODO process response

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UUID> listSessions() throws FlareSolverrException, InterruptedException {
        // TODO implement

        return List.of();
    }
}
