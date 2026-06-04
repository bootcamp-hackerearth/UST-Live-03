package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
