package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class TemplateHospedesState implements StateHandler {

    private static final DateTimeFormatter DATE_FORMATTER_SHORT = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter DATE_FORMATTER_LONG = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public StateResult handle(ConversationContext context, String incoming) {
        if (incoming == null || incoming.trim().isEmpty()) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_HOSPEDES)
                    .replyMessage("Por favor, preencha o template e envie novamente.")
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
        int qtdHospedes = hospedagem.getQtdHospedes();

        // Try to parse with labels first
        ParsedData parsedData = parseWithLabels(incoming, qtdHospedes);
        
        // If labeled parsing failed, try without labels
        if (parsedData == null) {
            parsedData = parseWithoutLabels(incoming, qtdHospedes);
        }

        if (parsedData == null) {
            return StateResult.builder()
                    .nextState(ConversationState.TEMPLATE_HOSPEDES)
                    .replyMessage("Formato inválido. Por favor preencha o template corretamente.")
                    .build();
        }

        // Update hospedagem
        hospedagem.setCheckinDate(parsedData.checkin);
        hospedagem.setCheckoutDate(parsedData.checkout);
        hospedagem.setObservacao(parsedData.observacao.toString());
        context.getHospedagemRepository().save(hospedagem);

        return StateResult.builder()
                .nextState(ConversationState.PERGUNTA_VEICULO)
                .replyMessage("Possui veículo?\n1 - Sim\n2 - Não")
                .build();
    }

    private ParsedData parseWithLabels(String text, int qtdHospedes) {
        StringBuilder observacao = new StringBuilder("Hóspedes:\n");
        LocalDate checkin = null;
        LocalDate checkout = null;

        // Parse guests with labels
        for (int i = 1; i <= qtdHospedes; i++) {
            Pattern nomePattern = Pattern.compile("(?i)hóspede\\s+" + i + "\\s+nome\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);
            Pattern docPattern = Pattern.compile("(?i)cpf\\s+ou\\s+passaporte\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);

            Matcher nomeMatcher = nomePattern.matcher(text);
            Matcher docMatcher = docPattern.matcher(text);

            if (nomeMatcher.find() && docMatcher.find()) {
                String nome = nomeMatcher.group(1).trim();
                String documento = docMatcher.group(1).trim();
                observacao.append(String.format("- %s (CPF/Passaporte: %s)\n", nome, documento));
            } else {
                return null; // Labeled format not matched
            }
        }

        // Parse dates with labels
        Pattern entradaPattern = Pattern.compile("(?i)entrada\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);
        Pattern saidaPattern = Pattern.compile("(?i)saída\\s*:\\s*(.+?)(?=\\n|$)", Pattern.DOTALL);

        Matcher entradaMatcher = entradaPattern.matcher(text);
        Matcher saidaMatcher = saidaPattern.matcher(text);

        if (entradaMatcher.find()) {
            checkin = parseDate(entradaMatcher.group(1).trim());
            if (checkin == null) return null;
        }

        if (saidaMatcher.find()) {
            checkout = parseDate(saidaMatcher.group(1).trim());
            if (checkout == null) return null;
        }

        ParsedData data = new ParsedData();
        data.observacao = observacao;
        data.checkin = checkin;
        data.checkout = checkout;
        return data;
    }

    private ParsedData parseWithoutLabels(String text, int qtdHospedes) {
        String[] lines = text.split("\\n");
        List<String> nonEmptyLines = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                nonEmptyLines.add(line.trim());
            }
        }

        if (nonEmptyLines.size() < qtdHospedes * 2 + 2) {
            return null;
        }

        StringBuilder observacao = new StringBuilder("Hóspedes:\n");
        for (int i = 0; i < qtdHospedes; i++) {
            String nome = nonEmptyLines.get(i * 2);
            String documento = nonEmptyLines.get(i * 2 + 1);
            observacao.append(String.format("- %s (CPF/Passaporte: %s)\n", nome, documento));
        }

        LocalDate checkin = parseDate(nonEmptyLines.get(qtdHospedes * 2));
        LocalDate checkout = parseDate(nonEmptyLines.get(qtdHospedes * 2 + 1));

        if (checkin == null || checkout == null) {
            return null;
        }

        ParsedData data = new ParsedData();
        data.observacao = observacao;
        data.checkin = checkin;
        data.checkout = checkout;
        return data;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER_LONG);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(dateStr, DATE_FORMATTER_SHORT);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    private static class ParsedData {
        StringBuilder observacao;
        LocalDate checkin;
        LocalDate checkout;
    }
}
