package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class InicioState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        SessaoWhatsapp sessao = context.getSessao();
        // Send greeting and ask for apartment
        String reply = "Olá! Vamos registrar sua hospedagem. Por favor informe o número do apartamento:";
        return StateResult.builder()
                .nextState(ConversationState.APARTAMENTO)
                .replyMessage(reply)
                .build();
    }
}

