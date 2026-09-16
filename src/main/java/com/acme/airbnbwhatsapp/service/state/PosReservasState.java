package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PosReservasState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();

        if (input.equals("1")) {
            String phoneNumber = context.getSessao().getPhoneNumber();
            Optional<com.acme.airbnbwhatsapp.domain.model.Responsavel> responsavelOpt = 
                context.getResponsavelRepository().findByTelefone(phoneNumber);

            if (responsavelOpt.isEmpty()) {
                return StateResult.builder()
                        .nextState(ConversationState.FINALIZADO)
                        .replyMessage("Responsável não encontrado. Por favor faça seu cadastro.")
                        .build();
            }

            String apartamentosStr = responsavelOpt.get().getApartamentos();
            String[] apartamentos = apartamentosStr.split(",");

            if (apartamentos.length > 1) {
                StringBuilder message = new StringBuilder("Olá! Vamos registrar a reserva do seu apartamento.\n");
                message.append("Digite o número correspondente a ao apartamento:\n");
                for (int i = 0; i < apartamentos.length; i++) {
                    message.append(String.format("%d - %s\n", i + 1, apartamentos[i].trim()));
                }
                
                return StateResult.builder()
                        .nextState(ConversationState.SELECAO_APARTAMENTO)
                        .replyMessage(message.toString())
                        .build();
            } else {
                return StateResult.builder()
                        .nextState(ConversationState.TEMPLATE_REGISTRO)
                        .replyMessage("")
                        .build();
            }
        } else if (input.equals("2")) {
            return StateResult.builder()
                    .nextState(ConversationState.FINALIZADO)
                    .replyMessage("Obrigado!!! Precisando de ajuda contate a recepção:\n21964841984")
                    .build();
        } else {
            return StateResult.builder()
                    .nextState(ConversationState.POS_RESERVAS)
                    .replyMessage("Opção inválida. Por favor digite:\n1 - Deseja fazer uma nova reserva?\n2 - Encerrar")
                    .build();
        }
    }
}
