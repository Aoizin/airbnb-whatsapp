package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DataEntradaState implements StateHandler {

    private final DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("d/M/yyyy");

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();
        LocalDate date;
        try {
            date = LocalDate.parse(text, fmt1);
        } catch (DateTimeParseException ex) {
            try {
                date = LocalDate.parse(text);
            } catch (Exception ex2) {
                return StateResult.builder()
                        .nextState(ConversationState.DATA_ENTRADA)
                        .replyMessage("Formato de data inválido. Envie no formato DD/MM/AAAA:")
                        .build();
            }
        }

        var hospOpt = context.getHospedagem();
        if (hospOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.APARTAMENTO)
                    .replyMessage("Não encontrei a hospedagem. Por favor informe o número do apartamento:")
                    .build();
        }

        Hospedagem h = hospOpt.get();
        h.setCheckinDate(date);
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.DATA_SAIDA)
                .replyMessage("Ótimo. Agora envie a data de saída (formato DD/MM/AAAA):")
                .build();
    }
}

