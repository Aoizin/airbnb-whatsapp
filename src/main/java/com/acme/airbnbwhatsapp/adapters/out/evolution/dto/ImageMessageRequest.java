package com.acme.airbnbwhatsapp.adapters.out.evolution.dto;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageMessageRequest {
    private String to;
    private Content image;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private String url;
        private String caption;
        private Map<String, Object> meta;
    }
}

