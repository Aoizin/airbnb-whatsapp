package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class QtdHospedesState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();
        int n;
        try {
            n = Integer.parseInt(text);
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            return StateResult.builder()
                    .nextState(ConversationState.QTD_HOSPEDES)
                    .replyMessage("Quantidade inválida. Informe apenas um número (ex: 2):")
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
        h.setQtdHospedes(n);
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.NOME_HOSPEDE)
                .replyMessage("Por favor informe o nome completo do hóspede principal:")
                .build();
    }
}

