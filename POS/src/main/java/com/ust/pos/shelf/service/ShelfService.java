package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    void delete(String username);

    WsDto<ShelfDto> findAll(Pageable pageable);

    ShelfDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
