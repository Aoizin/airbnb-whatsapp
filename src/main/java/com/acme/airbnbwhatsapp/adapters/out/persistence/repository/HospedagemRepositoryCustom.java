package com.acme.airbnbwhatsapp.adapters.out.persistence.repository;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HospedagemRepositoryCustom {
    Page<Hospedagem> findByFilters(String apartamento,
                                   String responsavel,
                                   HospedagemStatus status,
                                   LocalDate checkinStart,
                                   LocalDate checkinEnd,
                                   Integer minGuests,
                                   String search,
                                   Pageable pageable);
}

