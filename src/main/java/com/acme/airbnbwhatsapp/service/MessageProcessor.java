package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.HospedagemRepository;
import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.HospedeRepository;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProcessor {
    private final StateFactory stateFactory;
    private final HospedeRepository hospedeRepository;
    private final HospedagemRepository hospedagemRepository;

    public StateResult process(ConversationContext context, String incoming) {
        ConversationState current = context.getSessao().getState();
        // obtain handler and delegate
        var handler = stateFactory.get(current);
        return handler.handle(context, incoming);
    }

    public HospedeRepository getHospedeRepository() { return hospedeRepository; }
    public HospedagemRepository getHospedagemRepository() { return hospedagemRepository; }
}

