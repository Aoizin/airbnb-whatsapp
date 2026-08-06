package com.acme.airbnbwhatsapp.application.dto;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoWhatsappDTO {
    private UUID id;
    private String externalId;
    private String phoneNumber;
    private ConversationState state;
    private Instant startedAt;
    private Instant lastActivityAt;
    private UUID hospedeId;
    private UUID hospedagemId;
}

