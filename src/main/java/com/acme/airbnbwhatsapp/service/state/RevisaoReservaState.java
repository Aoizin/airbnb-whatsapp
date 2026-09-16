package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RevisaoReservaState implements StateHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        var hospedagemOpt = context.getHospedagem();
        if (hospedagemOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.FINALIZADO)
                    .replyMessage("Erro ao encontrar hospedagem. Por favor inicie novamente.")
                    .build();
        }

        Hospedagem hospedagem = hospedagemOpt.get();
        StringBuilder review = new StringBuilder();

        // Responsável
        if (hospedagem.getResponsavel() != null) {
            review.append("Responsável: ").append(hospedagem.getResponsavel().getNome()).append("\n");
        }

        // Apartamento
        if (hospedagem.getApartamento() != null) {
            review.append("Apartamento: ").append(hospedagem.getApartamento()).append("\n");
        }

        // Datas
        if (hospedagem.getCheckinDate() != null) {
            review.append("Entrada: ").append(hospedagem.getCheckinDate().format(DATE_FORMATTER)).append("\n");
        }
        if (hospedagem.getCheckoutDate() != null) {
            review.append("Saída: ").append(hospedagem.getCheckoutDate().format(DATE_FORMATTER)).append("\n");
        }

        // Hóspedes e Veículo (da observação)
        if (hospedagem.getObservacao() != null) {
            review.append(hospedagem.getObservacao());
        }

        return StateResult.builder()
                .nextState(ConversationState.CONFIRMACAO_RESERVA)
                .replyMessages(java.util.List.of(
                    review.toString(),
                    "Itens estão corretos?\n1 - Sim\n2 - Não"
                ))
                .build();
    }
}
