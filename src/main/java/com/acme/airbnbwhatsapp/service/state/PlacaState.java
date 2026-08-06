package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class PlacaState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();
        var hospOpt = context.getHospedagem();
        if (hospOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.APARTAMENTO)
                    .replyMessage("Não encontrei a hospedagem. Por favor informe o número do apartamento:")
                    .build();
        }

        Hospedagem h = hospOpt.get();
        if (text.equalsIgnoreCase("não") || text.equalsIgnoreCase("nao") || text.isEmpty()) {
            h.setPlaca(null);
        } else {
            h.setPlaca(text);
        }
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.OBSERVACAO)
                .replyMessage("Alguma observação adicional? (ou digite 'não')")
                .build();
    }
}

