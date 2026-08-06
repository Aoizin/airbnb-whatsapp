package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.HospedagemRepository;
import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.HospedeRepository;
import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.SessaoWhatsappRepository;
import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.Hospede;
import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ConversationContext {
    private final SessaoWhatsapp sessao;
    private final HospedeRepository hospedeRepository;
    private final HospedagemRepository hospedagemRepository;
    private final SessaoWhatsappRepository sessaoWhatsappRepository;

    public ConversationContext(SessaoWhatsapp sessao,
                               HospedeRepository hospedeRepository,
                               HospedagemRepository hospedagemRepository,
                               SessaoWhatsappRepository sessaoWhatsappRepository) {
        this.sessao = sessao;
        this.hospedeRepository = hospedeRepository;
        this.hospedagemRepository = hospedagemRepository;
        this.sessaoWhatsappRepository = sessaoWhatsappRepository;
    }

    public Optional<Hospedagem> getHospedagem() {
        return hospedagemRepository.findBySessaoWhatsappId(sessao.getId());
    }

    public Optional<Hospede> getHospede() {
        return Optional.ofNullable(sessao.getHospede());
    }

    public HospedeRepository getHospedeRepository() { return hospedeRepository; }
    public HospedagemRepository getHospedagemRepository() { return hospedagemRepository; }
    public SessaoWhatsappRepository getSessaoWhatsappRepository() { return sessaoWhatsappRepository; }
}

