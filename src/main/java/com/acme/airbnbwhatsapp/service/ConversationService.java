package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.SessaoWhatsappRepository;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final SessaoWhatsappRepository sessaoWhatsappRepository;
    private final MessageProcessor messageProcessor;

    @Transactional
    public String processInbound(String externalId, String phoneNumber, String text) {
        SessaoWhatsapp sessao = sessaoWhatsappRepository.findByExternalId(externalId)
                .orElseGet(() -> createSession(externalId, phoneNumber));

        // update last activity
        sessao.setLastActivityAt(Instant.now());
        sessaoWhatsappRepository.save(sessao);

        ConversationContext context = new ConversationContext(sessao,
                messageProcessor.getHospedeRepository(),
                messageProcessor.getHospedagemRepository(),
                sessaoWhatsappRepository);

        StateResult result = messageProcessor.process(context, text);

        // persist new state
        if (result.getNextState() != null) {
            sessao.setState(result.getNextState());
            sessaoWhatsappRepository.save(sessao);
        }

        return result.getReplyMessage();
    }

    private SessaoWhatsapp createSession(String externalId, String phoneNumber) {
        SessaoWhatsapp s = SessaoWhatsapp.builder()
                .externalId(externalId)
                .phoneNumber(phoneNumber)
                .state(ConversationState.INICIO)
                .startedAt(Instant.now())
                .lastActivityAt(Instant.now())
                .build();
        return sessaoWhatsappRepository.save(s);
    }
}

