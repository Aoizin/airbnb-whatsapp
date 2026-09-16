package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class DetalhesVeiculoState implements StateHandler {

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        if (incoming == null || incoming.trim().isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.DETALHES_VEICULO)
                    .replyMessage("Por favor, preencha os dados do veículo:")
                    .build();
        }

        var hospedagemOpt = context.getHospedagem();
        if (hospedagemOpt.isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.FINALIZADO)
                    .replyMessage("Erro ao encontrar hospedagem. Por favor inicie novamente.")
                    .build();
        }

        Hospedagem hospedagem = hospedagemOpt.get();

        // Try to parse with labels first
        VehicleData vehicleData = parseWithLabels(incoming);
        
        // If labeled parsing failed, try without labels
        if (vehicleData == null) {
            vehicleData = parseWithoutLabels(incoming);
        }

        if (vehicleData == null) {
            return StateResult.builder()
                    .nextState(ConversationState.DETALHES_VEICULO)
                    .replyMessage("Formato inválido. Por favor preencha os dados do veículo:")
                    .build();
        }

        // Update hospedagem with vehicle details
        String currentObservacao = hospedagem.getObservacao() != null ? hospedagem.getObservacao() : "";
        hospedagem.setObservacao(currentObservacao + String.format("\nVeículo:\n- Modelo: %s\n- Cor: %s\n- Placa: %s", 
            vehicleData.modelo, vehicleData.cor, vehicleData.placa));
        context.getHospedagemRepository().save(hospedagem);

        return StateResult.builder()
                .nextState(ConversationState.REVISAO_RESERVA)
                .replyMessage("")
                .build();
    }

    private VehicleData parseWithLabels(String text) {
        Pattern modeloPattern = Pattern.compile("(?i)modelo\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);
        Pattern corPattern = Pattern.compile("(?i)cor\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);
        Pattern placaPattern = Pattern.compile("(?i)placa\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);

        Matcher modeloMatcher = modeloPattern.matcher(text);
        Matcher corMatcher = corPattern.matcher(text);
        Matcher placaMatcher = placaPattern.matcher(text);

        if (modeloMatcher.find() && corMatcher.find() && placaMatcher.find()) {
            VehicleData data = new VehicleData();
            data.modelo = modeloMatcher.group(1).trim();
            data.cor = corMatcher.group(1).trim();
            data.placa = placaMatcher.group(1).trim();
            return data;
        }
        return null;
    }

    private VehicleData parseWithoutLabels(String text) {
        String[] lines = text.split("\\n");
        if (lines.length < 3) {
            return null;
        }

        VehicleData data = new VehicleData();
        data.modelo = lines[0].trim();
        data.cor = lines[1].trim();
        data.placa = lines[2].trim();
        return data;
    }

    private static class VehicleData {
        String modelo;
        String cor;
        String placa;
    }
}
