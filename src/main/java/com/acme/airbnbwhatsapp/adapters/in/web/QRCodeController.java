package com.acme.airbnbwhatsapp.adapters.in.web;

import com.acme.airbnbwhatsapp.adapters.out.evolution.EvolutionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/qrcode")
@RequiredArgsConstructor
public class QRCodeController {

    private final EvolutionClient evolutionClient;

    @GetMapping
    public String qrcodePage() {
        return "qrcode";
    }

    @GetMapping("/fetch")
    @ResponseBody
    public Map<String, Object> fetchQRCode() {
        Map<String, Object> result = evolutionClient.fetchQRCode();
        
        // If instance doesn't exist (404), try to create it
        if (result.isEmpty() || result.containsKey("error")) {
            evolutionClient.createInstance();
            // Configure webhook after creating instance
            evolutionClient.configureWebhook("http://host.docker.internal:8080/api/webhook/evolution");
            // Try fetching QR code again after creating instance
            result = evolutionClient.fetchQRCode();
        }
        
        return result;
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> getStatus() {
        return evolutionClient.getConnectionState();
    }

    @GetMapping("/configure-webhook")
    @ResponseBody
    public Map<String, String> configureWebhook() {
        boolean success = evolutionClient.configureWebhook("http://host.docker.internal:8080/api/webhook/evolution");
        return Map.of("status", success ? "configured" : "failed");
    }
}
