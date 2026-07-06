package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelvesRepository extends JpaRepository<Shelves, Long> {
    Shelves findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Shelves> findByStatus(String status);

    Page<Shelves> findByIsDeletedFalse(Pageable pageable);

    Page<Shelves> findAll(Specification<Shelves> example, Pageable pageable);
}
