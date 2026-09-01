package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemOrigem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateRegistroState implements StateHandler {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        if (incoming == null || incoming.trim().isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("Por favor, preencha o template e envie novamente.")
                    .build();
        }
        
        // Parse the template
        Map<String, String> fields = parseTemplate(incoming);
        
        // Validate required fields
        if (fields.get("responsavel") == null || fields.get("responsavel").trim().isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("Campo 'Responsável' é obrigatório. Por favor preencha o template corretamente:")
                    .build();
        }
        
        if (fields.get("apartamento") == null || !fields.get("apartamento").trim().matches("\\d+")) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("Campo 'Apartamento' deve conter apenas números. Por favor preencha o template corretamente:")
                    .build();
        }
        
        if (fields.get("nome_hospede") == null || fields.get("nome_hospede").trim().isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_REGISTRO)
                    .replyMessage("Campo 'Nome hóspede' é obrigatório. Por favor preencha o template corretamente:")
                    .build();
        }
        
        LocalDate checkin = null;
        LocalDate checkout = null;
        
        if (fields.get("checkin") != null && !fields.get("checkin").trim().isEmpty()) {
            try {
                checkin = LocalDate.parse(fields.get("checkin").trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                return StateResult.builder()
                        .nextState(ConversationState.TEMPLATE_REGISTRO)
                        .replyMessage("Data de Check-in inválida. Use o formato DD/MM/AAAA. Por favor preencha o template corretamente:")
                        .build();
            }
        }
        
        if (fields.get("checkout") != null && !fields.get("checkout").trim().isEmpty()) {
            try {
                checkout = LocalDate.parse(fields.get("checkout").trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                return StateResult.builder()
                        .nextState(ConversationState.TEMPLATE_REGISTRO)
                        .replyMessage("Data de Check-out inválida. Use o formato DD/MM/AAAA. Por favor preencha o template corretamente:")
                        .build();
            }
        }
        
        // Create or update hospedagem
        SessaoWhatsapp sessao = context.getSessao();
        var hospedagemOpt = context.getHospedagem();
        Hospedagem hospedagem = hospedagemOpt.orElseGet(() -> {
            Hospedagem h = Hospedagem.builder()
                    .sessaoWhatsapp(sessao)
                    .status(HospedagemStatus.PENDING)
                    .origem(HospedagemOrigem.WHATSAPP)
                    .createdAt(Instant.now())
                    .build();
            return h;
        });
        
        hospedagem.setResponsavel(fields.get("responsavel").trim());
        hospedagem.setApartamento(fields.get("apartamento").trim());
        hospedagem.setCheckinDate(checkin);
        hospedagem.setCheckoutDate(checkout);
        
        context.getHospedagemRepository().save(hospedagem);
        
        return StateResult.builder()
                .nextState(ConversationState.QTD_HOSPEDES)
                .replyMessage("Dados registrados com sucesso! Agora informe a quantidade de hóspedes:")
                .build();
    }
    
    private Map<String, String> parseTemplate(String text) {
        Map<String, String> fields = new java.util.HashMap<>();
        
        fields.put("responsavel", extractField(text, "Responsável:", "Apartamento:"));
        fields.put("apartamento", extractField(text, "Apartamento:", "Nome hóspede:"));
        fields.put("nome_hospede", extractField(text, "Nome hóspede:", "Check-in:"));
        fields.put("checkin", extractField(text, "Check-in:", "Check-out:"));
        fields.put("checkout", extractField(text, "Check-out:", null));
        
        return fields;
    }
    
    private String extractField(String text, String startPattern, String endPattern) {
        int startIndex = text.indexOf(startPattern);
        if (startIndex == -1) return null;
        
        startIndex += startPattern.length();
        
        int endIndex;
        if (endPattern != null) {
            endIndex = text.indexOf(endPattern, startIndex);
            if (endIndex == -1) {
                // If end pattern not found, take everything to the end
                endIndex = text.length();
            }
        } else {
            endIndex = text.length();
        }
        
        String value = text.substring(startIndex, endIndex).trim();
        return value.isEmpty() ? null : value;
    }
}
