package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TemplateRegistroState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        // First entry - ask for guest count
        if (incoming == null || incoming.trim().isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("Número de Hóspedes?")
                    .build();
        }

        String input = incoming.trim();

        // Validate numeric input
        if (!input.matches("\\d+")) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("Número de Hóspedes?")
                    .build();
        }

        int qtdHospedes = Integer.parseInt(input);

        // Create or update hospedagem
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

        hospedagem.setQtdHospedes(qtdHospedes);
        context.getHospedagemRepository().save(hospedagem);

        // Generate dynamic template based on guest count
        StringBuilder template = new StringBuilder();
        for (int i = 1; i <= qtdHospedes; i++) {
            template.append(String.format("Hóspede %d Nome:\nCPF ou Passaporte:\n", i));
        }
        template.append("Entrada:\nSaída:\n(Data no formato DD/MM/AA)");

        return StateResult.builder()
                .nextState(ConversationState.TEMPLATE_HOSPEDES)
                .replyMessages(java.util.List.of(
                    "Copie e cole a mensagem abaixo preenchendo os dados:",
                    template.toString()
                ))
                .build();
    }
}
