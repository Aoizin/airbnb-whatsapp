package com.acme.airbnbwhatsapp.adapters.out.persistence.repository;

import com.acme.airbnbwhatsapp.domain.model.SessaoWhatsapp;
import com.acme.airbnbwhatsapp.domain.model.enums.ConversationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoWhatsappRepository extends JpaRepository<SessaoWhatsapp, UUID> {
    Optional<SessaoWhatsapp> findByExternalId(String externalId);
    List<SessaoWhatsapp> findByPhoneNumber(String phoneNumber);
    List<SessaoWhatsapp> findByState(ConversationState state);
}

