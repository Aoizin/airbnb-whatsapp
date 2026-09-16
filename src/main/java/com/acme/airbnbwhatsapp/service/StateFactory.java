package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.state.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class StateFactory {
    private final EnumMap<ConversationState, com.acme.airbnbwhatsapp.service.state.StateHandler> map = new EnumMap<>(ConversationState.class);

    public StateFactory(InicioState inicio,
                        CadastroResponsavelState cadastroResponsavel,
                        MenuPrincipalState menuPrincipal,
                        PosReservasState posReservas,
                        SelecaoApartamentoState selecaoApartamento,
                        TemplateRegistroState templateRegistro,
                        TemplateHospedesState templateHospedes,
                        PerguntaVeiculoState perguntaVeiculo,
                        DetalhesVeiculoState detalhesVeiculo,
                        RevisaoReservaState revisaoReserva,
                        ConfirmacaoReservaState confirmacaoReserva,
                        AlteracaoReservaState alteracaoReserva,
                        ApartamentoState apartamento,
                        DataEntradaState dataEntrada,
                        DataSaidaState dataSaida,
                        ResponsavelState responsavel,
                        QtdHospedesState qtdHospedes,
                        NomesHospedesState nomesHospedes,
                        NomeHospedeState nomeHospede,
                        PlacaState placa,
                        ObservacaoState observacao,
                        FinalizadoState finalizado) {
        map.put(ConversationState.INICIO, inicio);
        map.put(ConversationState.CADASTRO_RESPONSAVEL, cadastroResponsavel);
        map.put(ConversationState.MENU_PRINCIPAL, menuPrincipal);
        map.put(ConversationState.POS_RESERVAS, posReservas);
        map.put(ConversationState.SELECAO_APARTAMENTO, selecaoApartamento);
        map.put(ConversationState.TEMPLATE_REGISTRO, templateRegistro);
        map.put(ConversationState.TEMPLATE_HOSPEDES, templateHospedes);
        map.put(ConversationState.PERGUNTA_VEICULO, perguntaVeiculo);
        map.put(ConversationState.DETALHES_VEICULO, detalhesVeiculo);
        map.put(ConversationState.REVISAO_RESERVA, revisaoReserva);
        map.put(ConversationState.CONFIRMACAO_RESERVA, confirmacaoReserva);
        map.put(ConversationState.ALTERACAO_RESERVA, alteracaoReserva);
        map.put(ConversationState.APARTAMENTO, apartamento);
        map.put(ConversationState.DATA_ENTRADA, dataEntrada);
        map.put(ConversationState.DATA_SAIDA, dataSaida);
        map.put(ConversationState.RESPONSAVEL, responsavel);
        map.put(ConversationState.QTD_HOSPEDES, qtdHospedes);
        map.put(ConversationState.NOMES_HOSPEDES, nomesHospedes);
        map.put(ConversationState.NOME_HOSPEDE, nomeHospede);
        map.put(ConversationState.PLACA, placa);
        map.put(ConversationState.OBSERVACAO, observacao);
        map.put(ConversationState.FINALIZADO, finalizado);
    }

    public com.acme.airbnbwhatsapp.service.state.StateHandler get(ConversationState state) {
        return map.getOrDefault(state, map.get(ConversationState.INICIO));
    }
}

