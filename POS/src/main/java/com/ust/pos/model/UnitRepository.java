package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Unit> findAllByStatus(boolean status);
}
