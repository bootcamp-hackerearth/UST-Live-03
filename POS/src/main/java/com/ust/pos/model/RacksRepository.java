package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface RacksRepository extends JpaRepository<Racks,Long>, QueryByExampleExecutor<Racks> {
    Racks findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Racks findByIdentifierAndDeletedFalse(String identifier);

    List<Racks> findByDeletedFalse();

    Page<Racks> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Page<Racks> findByDeletedFalse(Pageable pageable);
}