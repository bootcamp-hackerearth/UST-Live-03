package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfsRepository extends JpaRepository<Shelfs, Long> {

    void deleteByIdentifier(String identifier);

    Shelfs findByIdentifier(String identifier);

    Page<Shelfs> findByDeletedFalse(Pageable pageable);

}
