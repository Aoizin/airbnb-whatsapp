package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

@Component
public class AlteracaoReservaState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();

        switch (input) {
            case "1":
                // Alterar Apartamento
                return StateResult.builder()
                        .nextState(ConversationState.SELECAO_APARTAMENTO)
                        .replyMessage("Selecione o apartamento:")
                        .build();
            case "2":
                // Alterar Datas
                return StateResult.builder()
                        .nextState(ConversationState.TEMPLATE_HOSPEDES)
                        .replyMessage("Informe as novas datas:")
                        .build();
            case "3":
                // Alterar Hóspedes
                return StateResult.builder()
                        .nextState(ConversationState.TEMPLATE_REGISTRO)
                        .replyMessage("Número de Hóspedes?")
                        .build();
            case "4":
                // Alterar Veículo
                return StateResult.builder()
                        .nextState(ConversationState.PERGUNTA_VEICULO)
                        .replyMessage("Possui veículo?\n1 - Sim\n2 - Não")
                        .build();
            default:
                return StateResult.builder()
                        .nextState(ConversationState.ALTERACAO_RESERVA)
                        .replyMessage("Opção inválida. Por favor selecione:\n1 - Apartamento\n2 - Datas\n3 - Hóspedes\n4 - Veículo")
                        .build();
        }
    }
}
