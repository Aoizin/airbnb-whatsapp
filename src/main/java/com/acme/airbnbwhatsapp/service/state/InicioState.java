package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InicioState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        SessaoWhatsapp sessao = context.getSessao();
        String phoneNumber = sessao.getPhoneNumber();

        Optional<com.acme.airbnbwhatsapp.domain.model.Responsavel> responsavelOpt = 
            context.getResponsavelRepository().findByTelefone(phoneNumber);

        if (responsavelOpt.isEmpty()) {
            java.util.List<String> messages = java.util.List.of(
                "Olá, vamos fazer seu cadastro, complete as informações abaixo:",
                "Nome Completo:\nCPF:\nApartamento(s):"
            );
            return StateResult.builder()
                    .nextState(ConversationState.CADASTRO_RESPONSAVEL)
                    .replyMessages(messages)
                    .build();
        }

        com.acme.airbnbwhatsapp.domain.model.Responsavel responsavel = responsavelOpt.get();

        if (!responsavel.getCadastroAprovado()) {
            return StateResult.builder()
                    .nextState(ConversationState.FINALIZADO)
                    .replyMessage("Aguarde a aprovação do condomínio para fazer as suas reservas.\nPrecisando de ajuda contate a recepção:\n21964841984")
                    .build();
        }

        // Send menu options for approved users
        return StateResult.builder()
                .nextState(ConversationState.MENU_PRINCIPAL)
                .replyMessage("Olá, digite o número da opção desejada\n1 - Fazer uma nova reserva\n2- Verificar reservas")
                .build();
    }
}

