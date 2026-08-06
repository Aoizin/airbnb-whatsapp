package com.acme.airbnbwhatsapp.adapters.out.evolution.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMessageRequest {
    private String to;
    private Content document;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private String url;
        private String filename;
        private String mimeType;
    }
}

