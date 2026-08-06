package com.acme.airbnbwhatsapp.adapters.out.evolution;

/**
 * Port for outbound messaging.
 * Depends on abstraction, not concrete implementation.
 * Allows for testing with mocks and multiple implementations.
 */
public interface MessagingPort {
    boolean sendText(String to, String body);
    boolean sendImage(String to, String imageUrl, String caption);
    boolean sendDocument(String to, String docUrl, String filename, String mimeType);
    boolean sendLocation(String to, Double latitude, Double longitude, String name, String address);
}

