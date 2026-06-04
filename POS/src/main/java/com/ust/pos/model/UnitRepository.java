package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Unit> findByStatusIsTrue();

}
