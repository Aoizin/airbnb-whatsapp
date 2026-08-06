package com.acme.airbnbwhatsapp.adapters.in.web;

import com.acme.airbnbwhatsapp.application.dto.HospedagemSummaryDTO;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import com.acme.airbnbwhatsapp.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping({"","/dashboard"})
    public String dashboard(Model model) {
        long total = adminDashboardService.countTotal();
        long confirmed = adminDashboardService.countByStatus(HospedagemStatus.CONFIRMED);
        long pending = adminDashboardService.countByStatus(HospedagemStatus.PENDING);

        model.addAttribute("total", total);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("pending", pending);

        // show first page of hospedagens
        Page<HospedagemSummaryDTO> page = adminDashboardService.listHospedagens(null, null, null, null, null, null, null, 0, 10);
        model.addAttribute("hospedagens", page);

        return "admin/dashboard";
    }

    @GetMapping("/hospedagens")
    public String listHospedagens(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String apartamento,
                                 @RequestParam(required = false) String responsavel,
                                 @RequestParam(required = false) HospedagemStatus status,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinStart,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinEnd,
                                 @RequestParam(required = false) Integer minGuests,
                                 @RequestParam(required = false) String search) {

        Page<HospedagemSummaryDTO> result = adminDashboardService.listHospedagens(apartamento, responsavel, status, checkinStart, checkinEnd, minGuests, search, page, size);
        model.addAttribute("hospedagens", result);
        model.addAttribute("apartamento", apartamento);
        model.addAttribute("responsavel", responsavel);
        model.addAttribute("status", status);
        model.addAttribute("checkinStart", checkinStart);
        model.addAttribute("checkinEnd", checkinEnd);
        model.addAttribute("minGuests", minGuests);
        model.addAttribute("search", search);

        return "admin/hospedagens/list";
    }

    @GetMapping("/hospedagens/{id}")
    public String hospedagemDetail(Model model, @PathVariable("id") java.util.UUID id) {
        HospedagemSummaryDTO dto = adminDashboardService.getSummaryById(id);
        if (dto == null) return "redirect:/admin/hospedagens";
        model.addAttribute("h", dto);
        return "admin/hospedagens/detail";
    }
}

