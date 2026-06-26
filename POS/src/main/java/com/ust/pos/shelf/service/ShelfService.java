package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    WsDto<ShelfDto> findAll(Pageable pageable);

    List<ShelfDto> findAllActive();

    ShelfDto findByIdentifier(String identifier);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
