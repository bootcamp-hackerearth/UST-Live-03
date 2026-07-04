package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacksRepository extends JpaRepository<Racks, Long> {

    Racks findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Racks> findAll(Specification<Racks> example, Pageable pageable);

    List<Racks> findByStatus(boolean status);

    Page<Racks> findByIsDeletedFalse(Pageable pageable);

}
