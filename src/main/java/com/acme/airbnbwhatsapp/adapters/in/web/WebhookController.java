package com.acme.airbnbwhatsapp.adapters.in.web;

import com.acme.airbnbwhatsapp.service.OutboundMessagingService;
import jakarta.validation.Valid;
import com.acme.airbnbwhatsapp.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook/evolution")
@RequiredArgsConstructor
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final ConversationService conversationService;
    private final OutboundMessagingService messagingService;

    @PostMapping
    public ResponseEntity<?> receive(@Valid @RequestBody WebhookRequest req) {
        log.info("Received webhook from {} - text={}", req.getFrom(), req.getText());
        String reply = conversationService.processInbound(req.getExternalId(), req.getFrom(), req.getText());

        messagingService.sendReply(req.getFrom(), reply);

        // Always return 200 to acknowledge the webhook
        return ResponseEntity.ok().build();
    }
}

