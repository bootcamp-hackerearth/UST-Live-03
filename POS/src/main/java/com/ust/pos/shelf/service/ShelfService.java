package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    void delete(String identifier);

    List<ShelfDto> findAll(Pageable pageable);

    ShelfDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<Shelf> findActiveShelf();
}
