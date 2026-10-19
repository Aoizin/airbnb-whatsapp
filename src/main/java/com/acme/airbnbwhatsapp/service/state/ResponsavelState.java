package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.Responsavel;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResponsavelState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String text = (incoming == null) ? "" : incoming.trim();

        // Check for reset command
        if (text.equalsIgnoreCase("/reset")) {
            return StateResult.builder()
                    .nextState(ConversationState.INICIO)
                    .replyMessage("Conversação reiniciada. Vamos começar novamente.")
                    .build();
        }

        if (text.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.RESPONSAVEL)
                    .replyMessage("Nome do responsável inválido. Informe o nome e telefone do responsável:\n\nPara reiniciar a conversação, envie: /reset")
                    .build();
        }

        var hospOpt = context.getHospedagem();
        if (hospOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.APARTAMENTO)
                    .replyMessage("Não encontrei a hospedagem. Por favor informe o número do apartamento:")
                    .build();
        }

        Hospedagem h = hospOpt.get();
        
        // Parse name and phone from input (format: "Nome - telefone" or "Nome telefone")
        String[] parts = text.split("[-\\s]+");
        if (parts.length < 2) {
            return StateResult.builder()
                    .nextState(ConversationState.RESPONSAVEL)
                    .replyMessage("Formato inválido. Informe nome e telefone (ex: João - +5511xxxx):\n\nPara reiniciar a conversação, envie: /reset")
                    .build();
        }

        // Extract phone (last part should be the phone number)
        String phone = parts[parts.length - 1].trim();
        String name = String.join(" ", java.util.Arrays.copyOf(parts, parts.length - 1)).trim();

        // Try to find existing responsavel by phone
        Optional<Responsavel> respOpt = context.getResponsavelRepository().findByTelefone(phone);
        Responsavel responsavel;
        
        if (respOpt.isPresent()) {
            responsavel = respOpt.get();
            // Update name if different
            if (!responsavel.getNome().equals(name)) {
                responsavel.setNome(name);
                context.getResponsavelRepository().save(responsavel);
            }
        } else {
            // Create new responsavel with temporary CPF (will need to be completed later)
            responsavel = Responsavel.builder()
                    .nome(name)
                    .telefone(phone)
                    .cpf("PENDENTE") // Temporary CPF, needs to be completed
                    .apartamentos(h.getApartamento())
                    .cadastroAprovado(false)
                    .build();
            responsavel = context.getResponsavelRepository().save(responsavel);
        }

        // Associate responsavel with hospedagem
        h.setResponsavel(responsavel);
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.QTD_HOSPEDES)
                .replyMessage("Quantas pessoas irão se hospedar? Informe apenas o número:")
                .build();
    }
}

