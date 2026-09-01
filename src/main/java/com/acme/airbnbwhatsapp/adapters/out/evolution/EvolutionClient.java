package com.acme.airbnbwhatsapp.adapters.out.evolution;

import com.acme.airbnbwhatsapp.adapters.out.evolution.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EvolutionClient implements MessagingPort {
    private static final Logger log = LoggerFactory.getLogger(EvolutionClient.class);

    private final WebClient evolutionWebClient;
    private final EvolutionProperties props;

    /**
     * Generic method to send any type of message to Evolution API.
     * Follows template method pattern to reduce duplication.
     */
    private boolean sendMessage(String messageType, String to, Object payload) {
        try {
            var resp = evolutionWebClient.post()
                    .uri(getEndpoint(messageType))
                    .bodyValue(payload)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), r -> r.bodyToMono(String.class).map(s -> new RuntimeException(s)))
                    .bodyToMono(Map.class)
                    .block();
            log.info("Evolution API success messageType={} to={} response={}", messageType, to, resp);
            return true;
        } catch (WebClientResponseException wex) {
            log.error("Evolution API error messageType={} to={} status={} body={}",
                    messageType, to, wex.getStatusCode(), wex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Error sending {} to {}: {}", messageType, to, ex.getMessage(), ex);
        }
        return false;
    }

    private String getEndpoint(String messageType) {
        return switch (messageType) {
            case "text" -> "/message/sendText/airbnb-bot";
            case "image" -> "/message/sendImage/airbnb-bot";
            case "document" -> "/message/sendDocument/airbnb-bot";
            case "location" -> "/message/sendLocation/airbnb-bot";
            default -> throw new IllegalArgumentException("Unknown message type: " + messageType);
        };
    }

    public boolean sendText(String to, String body) {
        TextMessageRequest req = TextMessageRequest.builder()
                .number(to)
                .text(body)
                .build();
        return sendMessage("text", to, req);
    }

    public boolean sendImage(String to, String imageUrl, String caption) {
        ImageMessageRequest req = ImageMessageRequest.builder()
                .to(to)
                .image(ImageMessageRequest.Content.builder().url(imageUrl).caption(caption).build())
                .build();
        return sendMessage("image", to, req);
    }

    public boolean sendDocument(String to, String docUrl, String filename, String mimeType) {
        DocumentMessageRequest req = DocumentMessageRequest.builder()
                .to(to)
                .document(DocumentMessageRequest.Content.builder().url(docUrl).filename(filename).mimeType(mimeType).build())
                .build();
        return sendMessage("document", to, req);
    }

    public boolean sendLocation(String to, Double latitude, Double longitude, String name, String address) {
        LocationMessageRequest.Content content = LocationMessageRequest.Content.builder()
                .latitude(latitude)
                .longitude(longitude)
                .name(name)
                .address(address)
                .build();
        LocationMessageRequest req = LocationMessageRequest.builder().to(to).location(content).build();
        return sendMessage("location", to, req);
    }
}

