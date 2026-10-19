package com.acme.airbnbwhatsapp.service;

import com.acme.airbnbwhatsapp.application.dto.ResponsavelDTO;
import com.acme.airbnbwhatsapp.domain.model.Responsavel;
import com.acme.airbnbwhatsapp.adapters.out.persistence.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    public List<ResponsavelDTO> listAll() {
        return responsavelRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approve(UUID id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado"));
        responsavel.setCadastroAprovado(true);
        responsavelRepository.save(responsavel);
    }

    @Transactional
    public void revoke(UUID id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado"));
        responsavel.setCadastroAprovado(false);
        responsavelRepository.save(responsavel);
    }

    private ResponsavelDTO toDTO(Responsavel responsavel) {
        ResponsavelDTO dto = new ResponsavelDTO();
        dto.setId(responsavel.getId());
        dto.setNome(responsavel.getNome());
        dto.setTelefone(responsavel.getTelefone());
        dto.setCpf(responsavel.getCpf());
        dto.setApartamentos(responsavel.getApartamentos());
        dto.setCadastroAprovado(responsavel.getCadastroAprovado());
        return dto;
    }
}
