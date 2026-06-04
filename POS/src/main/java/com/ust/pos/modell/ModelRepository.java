package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);
}
