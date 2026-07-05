package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfsRepository extends JpaRepository<Shelfs, Long> {
    Shelfs findByIdentifier(String shelfs);

    void deleteByIdentifier(String identifier);

    List<Shelfs> findByStatusTrueAndIsDeletedFalse();

    Page<Shelfs> findByIsDeletedFalse(Pageable pageable);

    Page<Shelfs> findAll(Specification<Shelfs> example, Pageable pageable);

}
