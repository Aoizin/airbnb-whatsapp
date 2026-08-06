package com.acme.airbnbwhatsapp.adapters.out.persistence.repository;

import com.acme.airbnbwhatsapp.domain.model.Hospedagem;
import com.acme.airbnbwhatsapp.domain.model.enums.HospedagemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HospedagemRepositoryImpl implements HospedagemRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Hospedagem> findByFilters(String apartamento, String responsavel, HospedagemStatus status, LocalDate checkinStart, LocalDate checkinEnd, Integer minGuests, String search, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hospedagem> cq = cb.createQuery(Hospedagem.class);
        Root<Hospedagem> root = cq.from(Hospedagem.class);

        List<Predicate> predicates = new ArrayList<>();

        if (apartamento != null && !apartamento.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("apartamento")), "%" + apartamento.toLowerCase() + "%"));
        }
        if (responsavel != null && !responsavel.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("responsavel")), "%" + responsavel.toLowerCase() + "%"));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (checkinStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("checkinDate"), checkinStart));
        }
        if (checkinEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("checkinDate"), checkinEnd));
        }
        if (minGuests != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("qtdHospedes"), minGuests));
        }
        if (search != null && !search.isBlank()) {
            String s = "%" + search.toLowerCase() + "%";
            Predicate p1 = cb.like(cb.lower(root.get("apartamento")), s);
            Predicate p2 = cb.like(cb.lower(root.get("responsavel")), s);
            predicates.add(cb.or(p1, p2));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // ordering
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                Path<?> path = root.get(order.getProperty());
                orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
            });
            cq.orderBy(orders);
        } else {
            cq.orderBy(cb.desc(root.get("createdAt")));
        }

        TypedQuery<Hospedagem> query = em.createQuery(cq);
        int totalRows = query.getResultList().size();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Hospedagem> content = query.getResultList();

        return new PageImpl<>(content, pageable, totalRows);
    }
}

