package com.acme.airbnbwhatsapp.adapters.out.persistence.repository;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HospedagemRepository extends JpaRepository<Hospedagem, UUID>, HospedagemRepositoryCustom {
    List<Hospedagem> findByStatus(HospedagemStatus status);
    Optional<Hospedagem> findBySessaoWhatsappId(UUID sessaoId);
    long countByStatus(HospedagemStatus status);
    List<Hospedagem> findByResponsavelId(UUID responsavelId);
    
    @Query("SELECT h FROM Hospedagem h LEFT JOIN FETCH h.responsavel WHERE h.id = :id")
    Optional<Hospedagem> findByIdWithResponsavel(@Param("id") UUID id);
}



