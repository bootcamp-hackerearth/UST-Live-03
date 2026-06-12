package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelsRepository extends JpaRepository<Models, Long> {
    Models findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

}
