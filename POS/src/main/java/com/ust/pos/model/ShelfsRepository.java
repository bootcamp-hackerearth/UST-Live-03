package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfsRepository extends JpaRepository<Shelfs, Long> {

    void deleteByIdentifier(String identifier);

    Shelfs findByIdentifier(String identifier);
}
