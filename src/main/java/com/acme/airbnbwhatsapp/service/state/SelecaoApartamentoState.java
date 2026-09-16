package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SelecaoApartamentoState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();
        
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

        try {
            int selection = Integer.parseInt(input);
            if (selection < 1 || selection > apartamentos.length) {
                return StateResult.builder()
                        .nextState(ConversationState.SELECAO_APARTAMENTO)
                        .replyMessage("Opção inválida. Por favor selecione um número válido:")
                        .build();
            }

            String selectedApartamento = apartamentos[selection - 1].trim();

            // Create or update hospedagem
            var hospedagemOpt = context.getHospedagem();
            Hospedagem hospedagem = hospedagemOpt.orElseGet(() -> {
                Hospedagem h = Hospedagem.builder()
                        .sessaoWhatsapp(context.getSessao())
                        .responsavel(responsavelOpt.get())
                        .status(HospedagemStatus.PENDING)
                        .origem(HospedagemOrigem.WHATSAPP)
                        .createdAt(Instant.now())
                        .build();
                return h;
            });

            hospedagem.setApartamento(selectedApartamento);
            context.getHospedagemRepository().save(hospedagem);

            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("")
                    .build();

        } catch (NumberFormatException e) {
            return StateResult.builder()
                    .nextState(ConversationState.SELECAO_APARTAMENTO)
                    .replyMessage("Por favor digite um número válido:")
                    .build();
        }
    }
}
