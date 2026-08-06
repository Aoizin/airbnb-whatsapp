package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class ResponsavelState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();
        if (text.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.RESPONSAVEL)
                    .replyMessage("Nome do responsável inválido. Informe o nome e telefone do responsável:")
                    .build();
        }

        var hospOpt = context.getHospedagem();
        if (hospOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.APARTAMENTO)
                    .replyMessage("Não encontrei a hospedagem. Por favor informe o número do apartamento:")
                    .build();
        }

        Hospedagem h = hospOpt.get();
        h.setResponsavel(text);
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.QTD_HOSPEDES)
                .replyMessage("Quantas pessoas irão se hospedar? Informe apenas o número:")
                .build();
    }
}

