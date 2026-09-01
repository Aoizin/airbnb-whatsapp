package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.HospedagemRepository;
import com.acme.airbnbwhatsapp.application.dto.HospedagemSummaryDTO;
import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final HospedagemRepository hospedagemRepository;

    @Transactional(readOnly = true)
    public Page<HospedagemSummaryDTO> listHospedagens(String apartamento, String responsavel, HospedagemStatus status, LocalDate checkinStart, LocalDate checkinEnd, Integer minGuests, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hospedagem> pageModel = hospedagemRepository.findByFilters(apartamento, responsavel, status, checkinStart, checkinEnd, minGuests, search, pageable);
        return pageModel.map(this::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public HospedagemSummaryDTO getSummaryById(java.util.UUID id) {
        return hospedagemRepository.findById(id).map(this::toSummaryDto).orElse(null);
    }

    public long countTotal() { return hospedagemRepository.count(); }
    public long countByStatus(HospedagemStatus status) { return hospedagemRepository.countByStatus(status); }

    private HospedagemSummaryDTO toSummaryDto(Hospedagem h) {
        return HospedagemSummaryDTO.builder()
                .id(h.getId())
                .apartamento(h.getApartamento())
                .responsavel(h.getResponsavel())
                .checkinDate(h.getCheckinDate())
                .checkoutDate(h.getCheckoutDate())
                .qtdHospedes(h.getQtdHospedes())
                .status(h.getStatus())
                .build();
    }
}

