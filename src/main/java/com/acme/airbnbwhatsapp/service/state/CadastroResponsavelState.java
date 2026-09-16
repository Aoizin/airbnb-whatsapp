package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Responsavel;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CadastroResponsavelState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        String input = incoming.trim();
        
        String nome = null;
        String cpf = null;
        String apartamentos = null;

        // Try format 1: "Nome Completo: X\nCPF: Y\nApartamento(s): Z"
        Pattern pattern1 = Pattern.compile(
            "(?i)nome\\s+completo\\s*:\\s*(.+?)\\n" +
            "(?i)cpf\\s*:\\s*(.+?)\\n" +
            "(?i)apartamento\\(s\\)\\s*:\\s*(.+)",
            Pattern.DOTALL
        );
        Matcher matcher1 = pattern1.matcher(input);
        
        if (matcher1.find()) {
            nome = matcher1.group(1).trim();
            cpf = matcher1.group(2).trim();
            apartamentos = matcher1.group(3).trim();
        } else {
            // Try format 2: "X\nY\nZ" (3 lines)
            String[] lines = input.split("\\n");
            if (lines.length >= 3) {
                nome = lines[0].trim();
                cpf = lines[1].trim();
                apartamentos = lines[2].trim();
            }
        }

        // Validate parsed data
        if (nome == null || nome.isEmpty() || 
            cpf == null || cpf.isEmpty() || 
            apartamentos == null || apartamentos.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.CADASTRO_RESPONSAVEL)
                    .replyMessage("Formato inválido. Por favor envie as informações em um dos formatos:\n\n" +
                        "Formato 1:\nNome Completo: Gabriel Nicolau\nCPF: 41444750003\nApartamento(s): 203, 401\n\n" +
                        "Formato 2:\nGabriel Nicolau\n41444750003\n203, 401")
                    .build();
        }

        // Check if CPF already exists
        if (context.getResponsavelRepository().findByCpf(cpf).isPresent()) {
            return StateResult.builder()
                    .nextState(ConversationState.CADASTRO_RESPONSAVEL)
                    .replyMessage("Este CPF já está cadastrado. Por favor verifique ou contate o suporte.")
                    .build();
        }

        // Save responsavel
        SessaoWhatsapp sessao = context.getSessao();
        Responsavel responsavel = Responsavel.builder()
                .nome(nome)
                .cpf(cpf)
                .telefone(sessao.getPhoneNumber())
                .apartamentos(apartamentos)
                .cadastroAprovado(false)
                .build();
        
        context.getResponsavelRepository().save(responsavel);

        return StateResult.builder()
                .nextState(ConversationState.FINALIZADO)
                .replyMessage("Aguarde a aprovação do condomínio para fazer as suas reservas.\nPrecisando de ajuda contate a recepção:\n21964841984")
                .build();
    }
}
