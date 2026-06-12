package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfsRepository extends JpaRepository<Shelfs, Long> {

    Shelfs findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Shelfs> findByStatusIsTrue();

}