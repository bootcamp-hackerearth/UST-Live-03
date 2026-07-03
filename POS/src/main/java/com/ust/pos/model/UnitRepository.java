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
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    List<Unit> findAllByStatusAndDeletedFalse(Boolean status);

    Page<Unit> findByDeletedFalse(Pageable pageable);

    Page<Unit> findAll(Specification<Unit> example, Pageable pageable);
}
