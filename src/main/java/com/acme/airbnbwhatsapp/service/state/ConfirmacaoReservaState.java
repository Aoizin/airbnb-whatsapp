package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class ConfirmacaoReservaState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();

        if (input.equals("1")) {
            return StateResult.builder()
                    .nextState(ConversationState.FINALIZADO)
                    .replyMessage("Sua reserva já foi concluída!!!")
                    .build();
        } else if (input.equals("2")) {
            return StateResult.builder()
                    .nextState(ConversationState.ALTERACAO_RESERVA)
                    .replyMessage("O que deseja alterar:\n1 - Apartamento\n2 - Datas\n3 - Hóspedes\n4 - Veículo")
                    .build();
        } else {
            return StateResult.builder()
                    .nextState(ConversationState.CONFIRMACAO_RESERVA)
                    .replyMessage("Opção inválida. Por favor digite:\n1 - Sim\n2 - Não")
                    .build();
        }
    }
}
