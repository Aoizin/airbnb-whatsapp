package com.acme.airbnbwhatsapp.service.state;

import com.acme.airbnbwhatsapp.service.ConversationContext;
import com.acme.airbnbwhatsapp.service.StateResult;

/**
 * StateHandler represents a single state in the conversation state machine.
 */
public interface StateHandler {
    StateResult handle(ConversationContext context, String incoming);
}

