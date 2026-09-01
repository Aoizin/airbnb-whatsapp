package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.Hospede;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class ObservacaoState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();
        var hospOpt = context.getHospedagem();
        if (hospOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.APARTAMENTO)
                    .replyMessage("Não encontrei a hospedagem. Por favor informe o número do apartamento:")
                    .build();
        }

        Hospedagem h = hospOpt.get();
        if (text.equalsIgnoreCase("não") || text.equalsIgnoreCase("nao") || text.isEmpty()) {
            h.setObservacao(null);
        } else {
            h.setObservacao(text);
        }
        
        // Finalize the booking
        h.setStatus(HospedagemStatus.CONFIRMED);
        h.setUpdatedAt(Instant.now());
        SessaoWhatsapp sessao = context.getSessao();
        h.setSessaoWhatsapp(sessao);
        context.getHospedagemRepository().save(h);

        String summary = buildSummary(h, context.getHospede().orElse(null));

        return StateResult.builder()
                .nextState(ConversationState.FINALIZADO)
                .replyMessage("✅ Reserva confirmada com sucesso!\n\n" + summary)
                .build();
    }

    private String buildSummary(Hospedagem h, Hospede hospede) {
        StringBuilder sb = new StringBuilder();
        sb.append("📍 Apartamento: ").append(h.getApartamento()).append("\n");
        sb.append("👤 Responsável: ").append(h.getResponsavel()).append("\n");
        if (h.getCheckinDate() != null) sb.append("📅 Check-in: ").append(h.getCheckinDate()).append("\n");
        if (h.getCheckoutDate() != null) sb.append("📅 Check-out: ").append(h.getCheckoutDate()).append("\n");
        if (hospede != null) sb.append("🏠 Hóspede: ").append(hospede.getNome()).append("\n");
        if (h.getQtdHospedes() != null) sb.append("👥 Pessoas: ").append(h.getQtdHospedes()).append("\n");
        if (h.getPlaca() != null) sb.append("🚗 Placa: ").append(h.getPlaca()).append("\n");
        if (h.getObservacao() != null) sb.append("📝 Obs: ").append(h.getObservacao()).append("\n");
        return sb.toString();
    }
}

