package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {
    List<ShelfDto> findAll(Pageable pageable);

    ShelfDto findByIdentifier(String identifier);

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    void delete(String identifier);

    void toggleStatus(String identifier);

    List<ShelfDto> findActiveStatus();

    List<ShelfDto> findActiveShelf();
}
