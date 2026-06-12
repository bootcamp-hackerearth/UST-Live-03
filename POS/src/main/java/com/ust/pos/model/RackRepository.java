package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Rack> findByStatusTrue();

    List<Rack> findByStatusIsTrue();
}
