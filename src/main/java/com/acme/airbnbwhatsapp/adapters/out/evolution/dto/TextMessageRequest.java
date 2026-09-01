package com.acme.airbnbwhatsapp.adapters.out.evolution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextMessageRequest {
    private String number;
    private String text;
}

