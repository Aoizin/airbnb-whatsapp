package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class PerguntaVeiculoState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();

        // Check for reset command
        if (input.equalsIgnoreCase("/reset")) {
            return StateResult.builder()
                    .nextState(ConversationState.INICIO)
                    .replyMessage("Conversação reiniciada. Vamos começar novamente.")
                    .build();
        }

        if (input.equals("1")) {
            return StateResult.builder()
                    .nextState(ConversationState.DETALHES_VEICULO)
                    .replyMessage("Modelo:\nCor:\nPlaca:")
                    .build();
        } else if (input.equals("2")) {
            return StateResult.builder()
                    .nextState(ConversationState.REVISAO_RESERVA)
                    .replyMessage("")
                    .build();
        } else {
            return StateResult.builder()
                    .nextState(ConversationState.PERGUNTA_VEICULO)
                    .replyMessage("Opção inválida. Por favor digite:\n1 - Sim\n2 - Não\n\nPara reiniciar a conversação, envie: /reset")
                    .build();
        }
    }
}
