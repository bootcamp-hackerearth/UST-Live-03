package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ModelsRepository extends JpaRepository<Models, Long> {
    Models findByIdentifier(String identifier);

    Page<Models> findByDeletedFalse(Pageable pageable);

    List<Models> findAllByStatusAndDeletedFalse(boolean status);

    Page<Models> findAll(Specification<Models> example, Pageable pageable);
}
