package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByIdentifier(String identifier);

    Model findByIdentifierAndDeletedFalse(String identifier);

    Page<Model> findAllByDeletedFalse(Pageable pageable);

    List<Model> findByStatusTrueAndDeletedFalse();

    Page<Model> findAll(Specification<Model> example, Pageable pageable);
}