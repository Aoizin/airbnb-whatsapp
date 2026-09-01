package com.acme.airbnbwhatsapp.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookRequest {
    private String event;
    private String instance;
    private Data data;
    private String destination;
    private String date_time;
    private String sender;
    private String server_url;
    private String apikey;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Data {
        private Key key;
        private String pushName;
        private String status;
        private Message message;
        private String messageType;
        private Long messageTimestamp;
        private String instanceId;
        private String source;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Key {
            @JsonProperty("remoteJid")
            private String remoteJid;
            private String id;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Message {
            private String conversation;
        }
    }

    // Helper methods to extract needed fields
    public String getFrom() {
        return data != null && data.getKey() != null ? data.getKey().getRemoteJid() : null;
    }

    public String getText() {
        return data != null && data.getMessage() != null ? data.getMessage().getConversation() : null;
    }

    public String getExternalId() {
        // Use phone number as external ID since message ID changes per message
        return data != null && data.getKey() != null ? data.getKey().getRemoteJid() : null;
    }
}

