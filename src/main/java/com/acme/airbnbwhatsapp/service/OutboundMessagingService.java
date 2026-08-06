package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.adapters.out.evolution.MessagingPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for outbound messaging.
 * Separated from conversation logic to follow SRP (Single Responsibility Principle).
 * Depends on MessagingPort abstraction, not concrete implementation.
 */
@Service
@RequiredArgsConstructor
public class OutboundMessagingService {
    private static final Logger log = LoggerFactory.getLogger(OutboundMessagingService.class);

    private final MessagingPort messagingPort;

    public void sendReply(String phoneNumber, String message) {
        try {
            boolean sent = messagingPort.sendText(phoneNumber, message);
            if (!sent) {
                log.warn("Failed to send reply to {} via messaging service", phoneNumber);
            }
        } catch (Exception ex) {
            log.error("Exception while sending reply to {}: {}", phoneNumber, ex.getMessage(), ex);
        }
    }

    public void sendImage(String phoneNumber, String imageUrl, String caption) {
        try {
            boolean sent = messagingPort.sendImage(phoneNumber, imageUrl, caption);
            if (!sent) {
                log.warn("Failed to send image to {}", phoneNumber);
            }
        } catch (Exception ex) {
            log.error("Exception while sending image to {}: {}", phoneNumber, ex.getMessage(), ex);
        }
    }

    public void sendDocument(String phoneNumber, String docUrl, String filename, String mimeType) {
        try {
            boolean sent = messagingPort.sendDocument(phoneNumber, docUrl, filename, mimeType);
            if (!sent) {
                log.warn("Failed to send document to {}", phoneNumber);
            }
        } catch (Exception ex) {
            log.error("Exception while sending document to {}: {}", phoneNumber, ex.getMessage(), ex);
        }
    }

    public void sendLocation(String phoneNumber, Double latitude, Double longitude, String name, String address) {
        try {
            boolean sent = messagingPort.sendLocation(phoneNumber, latitude, longitude, name, address);
            if (!sent) {
                log.warn("Failed to send location to {}", phoneNumber);
            }
        } catch (Exception ex) {
            log.error("Exception while sending location to {}: {}", phoneNumber, ex.getMessage(), ex);
        }
    }
}

