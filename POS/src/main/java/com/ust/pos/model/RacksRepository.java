package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RacksRepository extends JpaRepository<Racks, Long> {
    Racks findByIdentifier(String racks);

    void deleteByIdentifier(String identifier);

    List<Racks> findByStatus(boolean status);
}
