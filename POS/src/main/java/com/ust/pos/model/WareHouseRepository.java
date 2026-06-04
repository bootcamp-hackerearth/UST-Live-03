package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {
    WareHouse findByIdentifier(String username);

    void deleteByIdentifier(String username);

    List<WareHouse> findByStatusTrue();
}