package com.acme.airbnbwhatsapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "responsavel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responsavel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @NotBlank
    @Column(name = "telefone", nullable = false)
    private String telefone;

    @NotNull
    @Column(name = "apartamentos", nullable = false)
    private String apartamentos;

    @Column(name = "cadastro_aprovado", nullable = false)
    @Builder.Default
    private Boolean cadastroAprovado = false;

}
