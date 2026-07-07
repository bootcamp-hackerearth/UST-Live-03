package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {

    WareHouse findByIdentifier(String identifier);

    Page<WareHouse> findByDeletedFalse(Pageable pageable);

    List<WareHouse> findByStatusIsTrueAndDeletedFalse();

    Page<WareHouse> findAll(Specification<WareHouse> example, Pageable pageable);

}