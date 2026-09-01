package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospede;
import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

@Component
public class NomesHospedesState implements StateHandler {

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
        
        if (text.isEmpty()) {
            int qtd = h.getQtdHospedes() != null ? h.getQtdHospedes() : 1;
            return StateResult.builder()
                    .nextState(ConversationState.NOMES_HOSPEDES)
                    .replyMessage("Por favor informe os nomes de todos os hóspedes (separados por vírgula):")
                    .build();
        }

        // Parse names separated by comma
        String[] nomes = text.split(",");
        SessaoWhatsapp sessao = context.getSessao();
        String telefone = sessao.getPhoneNumber();
        
        // Create Hospede records for each guest
        StringBuilder nomesRegistrados = new StringBuilder();
        for (int i = 0; i < nomes.length; i++) {
            String nome = nomes[i].trim();
            if (!nome.isEmpty()) {
                Hospede hospede = Hospede.builder()
                        .nome(nome)
                        .telefone(telefone)
                        .criadoEm(Instant.now())
                        .build();
                context.getHospedeRepository().save(hospede);
                
                if (nomesRegistrados.length() > 0) {
                    nomesRegistrados.append(", ");
                }
                nomesRegistrados.append(nome);
            }
        }

        // Store the names in observacao for reference
        String currentObservacao = h.getObservacao() != null ? h.getObservacao() : "";
        String nomesInfo = "Hóspedes registrados: " + nomesRegistrados.toString();
        h.setObservacao(currentObservacao.isEmpty() ? nomesInfo : currentObservacao + "\n" + nomesInfo);
        context.getHospedagemRepository().save(h);

        return StateResult.builder()
                .nextState(ConversationState.PLACA)
                .replyMessage("Hóspedes registrados com sucesso! Se houver placa do veículo, informe agora (ou digite 'não'):")
                .build();
    }
}
