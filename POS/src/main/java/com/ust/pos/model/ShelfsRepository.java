package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfsRepository extends JpaRepository<Shelfs, Long> {

    Shelfs findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Shelfs> findByStatusTrue();
}
