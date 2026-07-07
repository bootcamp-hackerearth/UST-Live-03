package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Model findByIdentifier(String identifier);

    List<Model> findByStatusTrueAndDeletedFalse();

    Model findByIdentifierAndDeletedFalse (String identifier);

    Page<Model> findAllByDeletedFalse (Pageable pageable);

    Page<Model> findAll(Specification<Model> example, Pageable pageable);

}
