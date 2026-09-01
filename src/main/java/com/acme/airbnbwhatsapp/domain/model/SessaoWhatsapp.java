package com.acme.airbnbwhatsapp.domain.model;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessao_whatsapp", indexes = {
        @Index(name = "idx_sessao_phone", columnList = "phone_number"),
        @Index(name = "idx_sessao_external", columnList = "external_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoWhatsapp {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @NotBlank
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ConversationState state;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospede_id")
    private Hospede hospede;

    @OneToOne(mappedBy = "sessaoWhatsapp", fetch = FetchType.LAZY)
    private Hospedagem hospedagem;

}

