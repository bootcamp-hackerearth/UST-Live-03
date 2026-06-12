package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    WsDto<ShelfDto> findAll(Pageable pageable);

    void delete(String identifier);

    List<ShelfDto> getActiveShelves();

    ShelfDto toggleStatus(String identifier);
}