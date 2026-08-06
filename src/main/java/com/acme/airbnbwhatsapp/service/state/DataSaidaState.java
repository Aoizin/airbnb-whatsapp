package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DataSaidaState implements StateHandler {
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
                        .nextState(ConversationState.DATA_SAIDA)
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
        if (h.getCheckinDate() != null && !date.isAfter(h.getCheckinDate())) {
            return StateResult.builder()
                    .nextState(ConversationState.DATA_SAIDA)
                    .replyMessage("A data de saída deve ser posterior à data de entrada. Envie uma data válida:")
                    .build();
        }

        h.setCheckoutDate(date);
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.RESPONSAVEL)
                .replyMessage("Quem será o responsável pela reserva? Informe nome e telefone (ex: João - +5511xxxx):")
                .build();
    }
}

