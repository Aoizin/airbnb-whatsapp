package com.acme.airbnbwhatsapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hospede", indexes = {
        @Index(name = "idx_hospede_phone", columnList = "telefone"),
        @Index(name = "idx_hospede_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospede {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String nome;

    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "documento")
    private String documento;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @OneToMany(mappedBy = "hospede", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hospedagem> hospedagens = new ArrayList<>();

}

