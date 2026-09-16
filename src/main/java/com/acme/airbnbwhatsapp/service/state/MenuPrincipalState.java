package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuPrincipalState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();

        if (input.equals("1")) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("")
                    .build();
        } else if (input.equals("2")) {
            String phoneNumber = context.getSessao().getPhoneNumber();
            Optional<com.acme.airbnbwhatsapp.domain.model.Responsavel> responsavelOpt = 
                context.getResponsavelRepository().findByTelefone(phoneNumber);

            if (responsavelOpt.isEmpty()) {
                return StateResult.builder()
                        .nextState(ConversationState.FINALIZADO)
                        .replyMessage("Responsável não encontrado. Por favor faça seu cadastro.")
                        .build();
            }

            List<Hospedagem> hospedagens = context.getHospedagemRepository()
                .findByResponsavelId(responsavelOpt.get().getId());

            StringBuilder message = new StringBuilder("Seguem suas reservas:\n");
            
            if (hospedagens.isEmpty()) {
                message.append("Nenhuma reserva encontrada.");
            } else {
                for (Hospedagem h : hospedagens) {
                    message.append(String.format(
                        "- Apartamento: %s\n  Check-in: %s\n  Check-out: %s\n  Status: %s\n",
                        h.getApartamento() != null ? h.getApartamento() : "N/A",
                        h.getCheckinDate() != null ? h.getCheckinDate() : "N/A",
                        h.getCheckoutDate() != null ? h.getCheckoutDate() : "N/A",
                        h.getStatus()
                    ));
                }
            }

            return StateResult.builder()
                    .nextState(ConversationState.POS_RESERVAS)
                    .replyMessage(message.toString() + "\n\n1 - Deseja fazer uma nova reserva?\n2 - Encerrar")
                    .build();
        } else {
            return StateResult.builder()
                    .nextState(ConversationState.MENU_PRINCIPAL)
                    .replyMessage("Opção inválida. Por favor digite:\n1 - Fazer uma nova reserva\n2- Verificar reservas")
                    .build();
        }
    }
}
