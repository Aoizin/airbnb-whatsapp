package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateResult {
    private ConversationState nextState;
    private String replyMessage;
    private java.util.List<String> replyMessages;
}

