package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospede;
import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NomeHospedeState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();
        if (text.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.NOME_HOSPEDE)
                    .replyMessage("Nome inválido. Informe o nome completo do hóspede:")
                    .build();
        }

        SessaoWhatsapp sessao = context.getSessao();

        Hospede hospede = context.getHospede().orElseGet(() -> {
            Hospede h = Hospede.builder()
                    .nome(text)
                    .telefone(sessao.getPhoneNumber())
                    .criadoEm(Instant.now())
                    .build();
            return h;
        });

        // update name if needed
        hospede.setNome(text);
        hospede.setTelefone(sessao.getPhoneNumber());
        hospede = context.getHospedeRepository().save(hospede);

        // link sessao to hospede
        sessao.setHospede(hospede);
        context.getSessaoWhatsappRepository().save(sessao);

        // ensure hospedagem exists
        var hospOpt = context.getHospedagem();
        if (hospOpt.isEmpty()) {
            Hospedagem h = Hospedagem.builder()
                    .sessaoWhatsapp(sessao)
                    .status(com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus.PENDING)
                    .origem(com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem.WHATSAPP)
                    .createdAt(Instant.now())
                    .build();
            context.getHospedagemRepository().save(h);
        }

        return StateResult.builder()
                .nextState(ConversationState.PLACA)
                .replyMessage("Se houver placa do veículo, informe agora (ou digite 'não'):")
                .build();
    }
}

