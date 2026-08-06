package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ApartamentoState implements StateHandler {
    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String apt = (incoming == null) ? "" : incoming.trim();
        if (apt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.APARTAMENTO)
                    .replyMessage("Número do apartamento inválido. Por favor informe o número do apartamento:")
                    .build();
        }

        SessaoWhatsapp sessao = context.getSessao();

        var hospedagemOpt = context.getHospedagem();
        Hospedagem hospedagem = hospedagemOpt.orElseGet(() -> {
            Hospedagem h = Hospedagem.builder()
                    .sessaoWhatsapp(sessao)
                    .status(HospedagemStatus.PENDING)
                    .origem(HospedagemOrigem.WHATSAPP)
                    .createdAt(Instant.now())
                    .build();
            return h;
        });

        hospedagem.setApartamento(apt);
        context.getHospedagemRepository().save(hospedagem);

        return StateResult.builder()
                .nextState(ConversationState.DATA_ENTRADA)
                .replyMessage("Perfeito. Agora envie a data de entrada (formato DD/MM/AAAA):")
                .build();
    }
}

