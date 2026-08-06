package com.acme.airbnbwhatsapp.application.dto;

import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospedagemDTO {
    private UUID id;
    private UUID hospedeId;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private HospedagemStatus status;
    private HospedagemOrigem origem;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID sessaoWhatsappId;
}

