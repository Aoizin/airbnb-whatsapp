package com.acme.airbnbwhatsapp.adapters.out.persistence.repository;

import com.acme.airbnbwhatsapp.domain.model.Hospede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HospedeRepository extends JpaRepository<Hospede, UUID> {
    Optional<Hospede> findByTelefone(String telefone);
    Optional<Hospede> findByEmail(String email);
}

