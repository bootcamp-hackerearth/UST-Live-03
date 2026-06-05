package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    List<ShelfDto> findAll(Pageable pageable);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    void delete(String identifier);

    ShelfDto toggleStatus(String identifier,boolean status);

    List<ShelfDto> findActiveShelves();
}