package com.acme.airbnbwhatsapp.config;

import com.acme.airbnbwhatsapp.adapters.out.evolution.EvolutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(EvolutionProperties.class)
public class EvolutionConfiguration {

    @Bean
    public WebClient evolutionWebClient(EvolutionProperties props) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("apikey", props.getApiKey())
                .exchangeStrategies(strategies)
                .build();
    }
}

