package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.state.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class StateFactory {
    private final EnumMap<ConversationState, com.acme.airbnbwhatsapp.service.state.StateHandler> map = new EnumMap<>(ConversationState.class);

    public StateFactory(InicioState inicio,
                        ApartamentoState apartamento,
                        DataEntradaState dataEntrada,
                        DataSaidaState dataSaida,
                        ResponsavelState responsavel,
                        QtdHospedesState qtdHospedes,
                        NomeHospedeState nomeHospede,
                        PlacaState placa,
                        ObservacaoState observacao,
                        FinalizadoState finalizado) {
        map.put(ConversationState.INICIO, inicio);
        map.put(ConversationState.APARTAMENTO, apartamento);
        map.put(ConversationState.DATA_ENTRADA, dataEntrada);
        map.put(ConversationState.DATA_SAIDA, dataSaida);
        map.put(ConversationState.RESPONSAVEL, responsavel);
        map.put(ConversationState.QTD_HOSPEDES, qtdHospedes);
        map.put(ConversationState.NOME_HOSPEDE, nomeHospede);
        map.put(ConversationState.PLACA, placa);
        map.put(ConversationState.OBSERVACAO, observacao);
        map.put(ConversationState.FINALIZADO, finalizado);
    }

    public com.acme.airbnbwhatsapp.service.state.StateHandler get(ConversationState state) {
        return map.getOrDefault(state, map.get(ConversationState.INICIO));
    }
}

