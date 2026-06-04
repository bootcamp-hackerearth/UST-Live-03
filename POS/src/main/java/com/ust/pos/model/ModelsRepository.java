package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelsRepository extends JpaRepository<Models, Long> {

    Models findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Models> findByStatusIsTrue();

    Page<Models> findAll(Pageable pageable);
}