package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelProductRepository extends JpaRepository<ModelProduct, Long> {

    ModelProduct findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
