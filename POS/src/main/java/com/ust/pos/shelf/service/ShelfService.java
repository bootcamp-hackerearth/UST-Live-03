package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findAll();

    void delete(String identifier);

    void toggleStatus(String identifier);

    List<ShelfDto> findActive();

    List<ShelfDto> findAll(Pageable pageable);
}