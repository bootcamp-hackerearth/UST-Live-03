package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long>, QueryByExampleExecutor<Unit> {
    Unit findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Unit findByIdentifierAndDeletedFalse(String identifier);

    List<Unit> findByDeletedFalse();

    Page<Unit> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Page<Unit> findByDeletedFalse(Pageable pageable);
}