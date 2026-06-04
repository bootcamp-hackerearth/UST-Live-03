package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    long deleteByIdentifier(String identifier);

    List<Rack> findByStatusTrue();

}