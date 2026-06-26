package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Model findByIdentifier(String identifier);

    List<Model> findByStatusTrueAndDeletedFalse();

    Model findByIdentifierAndDeletedFalse (String identifier);

    Page<Model> findALlByDeletedFalse (Pageable pageable);

}
