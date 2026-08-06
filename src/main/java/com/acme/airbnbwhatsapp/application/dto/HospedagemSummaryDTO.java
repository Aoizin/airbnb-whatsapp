package com.acme.airbnbwhatsapp.application.dto;

import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospedagemSummaryDTO {
    private UUID id;
    private String apartamento;
    private String responsavel;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer qtdHospedes;
    private HospedagemStatus status;
}

