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
public interface RackRepository extends JpaRepository<Rack, Long> {
    Rack findByIdentifier(String identifier);

    List<Rack> findAllByStatusAndDeletedFalse(boolean status);

    Page<Rack> findByDeletedFalse(Pageable pageable);

    Page<Rack> findAll(Specification<Rack> example, Pageable pageable);
}
