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
        // Send greeting and template
        java.util.List<String> messages = java.util.List.of(
            "Olá! Vamos registrar sua hospedagem. Copie e cole a mensagem abaixo preenchendo os dados:",
            "Responsável: \nApartamento: \nNome hóspede:\nCheck-in: \nCheck-out:"
        );
        return StateResult.builder()
                .nextState(ConversationState.TEMPLATE_REGISTRO)
                .replyMessages(messages)
                .build();
    }
}

