package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;

import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findAll();

    List<ShelfDto> findActiveShelves();

    Page<ShelfDto> findAll(String search, Pageable pageable);

    WsDto<ShelfDto> findAll(Pageable pageable);

    void delete(String identifier);

    void toggleStatus(String identifier);
}
