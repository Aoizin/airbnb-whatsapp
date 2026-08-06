package com.acme.airbnbwhatsapp.adapters.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookRequest {
    @NotBlank(message = "externalId must not be blank")
    private String externalId;

    @NotBlank(message = "from (phone number) must not be blank")
    private String from;

    @NotNull(message = "text must not be null")
    private String text;
}

