package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelsRepository extends JpaRepository<Models, Long> {

    Models findByIdentifier(String identifier);

    Models findByIdentifierAndDeletedFalse(String identifier);

    Page<Models> findAllByDeletedFalse(Pageable pageable);

    List<Models> findByStatusTrueAndDeletedFalse();

    Page<Models> findAll(Specification<Models> example, Pageable pageable);
}
