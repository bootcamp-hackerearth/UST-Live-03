package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Rack> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Rack> findByIsDeleted(boolean isDeleted, Pageable pageable);
}