package com.acme.airbnbwhatsapp.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsavelDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private String telefone;
    private String apartamentos;
    private Boolean cadastroAprovado;
}
