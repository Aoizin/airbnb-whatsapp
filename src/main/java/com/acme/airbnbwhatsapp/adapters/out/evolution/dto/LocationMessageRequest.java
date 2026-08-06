package com.acme.airbnbwhatsapp.adapters.out.evolution.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationMessageRequest {
    private String to;
    private Content location;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private Double latitude;
        private Double longitude;
        private String name;
        private String address;
    }
}

