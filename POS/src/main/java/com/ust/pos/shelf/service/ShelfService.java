package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto brandDto);

    ShelfDto update(ShelfDto brandDto);

    void delete(String identifier);

    List<ShelfDto> findAll();

    Page<ShelfDto> findAll(Pageable pageable, String search);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findAllByStatus();

    void toggleStatus(String identifier);
}
