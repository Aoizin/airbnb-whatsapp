package com.acme.airbnbwhatsapp.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospedeDTO {
    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private String documento;
    private Instant criadoEm;
}

