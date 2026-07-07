package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;

import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findAll();

    List<ShelfDto> findActiveShelves();

    Page<ShelfDto> findAll(Example<Shelf> example, Pageable pageable);

    WsDto<ShelfDto> findAll(Pageable pageable);

    void delete(String identifier);

    void toggleStatus(String identifier);
}
