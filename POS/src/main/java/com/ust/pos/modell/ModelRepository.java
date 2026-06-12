package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Model findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Model> findByStatusTrue();

}