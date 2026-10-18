package com.techtribe.techtribe_backend.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger logger =
            LoggerFactory.getLogger(GeminiService.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model}") String model,
            ObjectMapper objectMapper) {

        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public String generateContent(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "AI prompt cannot be empty."
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Gemini API key is missing."
            );
        }

        try {

            String endpoint =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model
                    + ":generateContent";

            Map<String, Object> requestData = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            String requestBody =
                    objectMapper.writeValueAsString(requestData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody,
                                    StandardCharsets.UTF_8
                            )
                    )
                    .build();

            logger.info(
                    "Sending request to Gemini model: {}",
                    model
            );

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int statusCode = response.statusCode();

            logger.info(
                    "Gemini API returned HTTP status: {}",
                    statusCode
            );

            if (statusCode != 200) {

                String errorMessage = "Gemini API request failed.";

                try {
                    JsonNode errorRoot =
                            objectMapper.readTree(response.body());

                    String apiMessage = errorRoot
                            .path("error")
                            .path("message")
                            .asText();

                    if (apiMessage != null && !apiMessage.isBlank()) {
                        errorMessage = apiMessage;
                    }

                } catch (Exception parseException) {
                    logger.warn(
                            "Unable to parse Gemini error response."
                    );
                }

                logger.error(
                        "Gemini API error. HTTP status: {}. Message: {}",
                        statusCode,
                        errorMessage
                );

                throw new IllegalStateException(
                        "Gemini API request failed. HTTP "
                        + statusCode
                        + ": "
                        + errorMessage
                );
            }

            JsonNode root = objectMapper.readTree(
                    response.body()
            );

            JsonNode textNode = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode()
                    || textNode.asText().isBlank()) {

                throw new IllegalStateException(
                        "Gemini returned an empty response."
                );
            }

            logger.info(
                    "Gemini generated content successfully."
            );

            return textNode.asText();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            logger.error(
                    "Gemini request interrupted.",
                    e
            );

            throw new IllegalStateException(
                    "Gemini request was interrupted.",
                    e
            );

        } catch (IOException e) {

            logger.error(
                    "Unable to communicate with Gemini API.",
                    e
            );

            throw new IllegalStateException(
                    "Unable to communicate with Gemini API.",
                    e
            );
        }
    }
}