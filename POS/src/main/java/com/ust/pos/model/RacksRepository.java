package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacksRepository extends JpaRepository<Racks,Long> {

    Racks findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Racks> findByStatusIsTrue();

}