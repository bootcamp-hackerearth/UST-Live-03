package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelsRepository extends JpaRepository<Models, Long> {
    Models findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Models> findByIsDeletedFalse(Pageable pageable);

    Page<Models> findAll(Specification<Models> example, Pageable pageable);

    Models findByIdentifierAndIsDeletedFalse(String identifier);
}
