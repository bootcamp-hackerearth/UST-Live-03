package com.ust.pos.model;

import com.ust.pos.dto.ShelfDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Shelf findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<ShelfDto> findByStatus(boolean status);
}
