package com.acme.airbnbwhatsapp.domain.model;

import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hospedagem", indexes = {
        @Index(name = "idx_hospedagem_status", columnList = "status"),
        @Index(name = "idx_hospedagem_hospede", columnList = "hospede_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospedagem {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospede;

    @NotNull
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @NotNull
    @Column(name = "checkout_date", nullable = false)
    private LocalDate checkoutDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HospedagemStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false)
    private HospedagemOrigem origem;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_whatsapp_id", unique = true)
    private SessaoWhatsapp sessaoWhatsapp;

    @Column(name = "apartamento")
    private String apartamento;

    @Column(name = "responsavel")
    private String responsavel;

    @Column(name = "qtd_hospedes")
    private Integer qtdHospedes;

    @Column(name = "placa")
    private String placa;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

}

