package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model> {
    Model findByIdentifier(String identifier);

    Page<Model> findByDeletedFalse(Pageable pageable);

    List<Model> findByStatusTrue();

    Page<Model> findAll(Specification spec, Pageable pageable);
}
