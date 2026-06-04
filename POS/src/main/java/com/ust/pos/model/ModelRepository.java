package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
