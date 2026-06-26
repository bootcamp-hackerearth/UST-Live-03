package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByIdentifier(String identifier);

    List<Model> findByStatusTrue();

    void deleteByIdentifier(String identifier);

    Page<Model> findByIsDeletedFalse(Pageable pageable);
}
