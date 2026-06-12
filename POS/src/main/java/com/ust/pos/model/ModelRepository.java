package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Model> findByStatusTrue();
}
