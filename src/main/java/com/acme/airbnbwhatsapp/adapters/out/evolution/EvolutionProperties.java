package com.acme.airbnbwhatsapp.adapters.out.evolution;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "evolution")
public class EvolutionProperties {
    private String baseUrl;
    private String apiKey;
    private Endpoints endpoints = new Endpoints();

    @Data
    public static class Endpoints {
        private String sendText;
        private String sendImage;
        private String sendDocument;
        private String sendLocation;
    }
}

