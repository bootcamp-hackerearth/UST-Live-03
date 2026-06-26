package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacksRepository extends JpaRepository<Racks, Long> {
    Racks findByIdentifier(String identifier);

    Racks findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Racks> findByIsDeleteFalse();

    Page<Racks> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<Racks> findByIsDeleteFalse(Pageable pageable);
}
