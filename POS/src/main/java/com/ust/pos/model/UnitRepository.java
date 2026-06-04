package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Unit findByIdentifier(String identifier);

    Unit deleteByIdentifier(String identifier);

    List<Unit> findByStatus(boolean status);
}
