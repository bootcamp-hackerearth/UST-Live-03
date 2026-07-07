package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    WsDto<ShelfDto> findAll(Pageable pageable);

    void delete(String identifier);

    List<ShelfDto> getActiveShelves();

    ShelfDto toggleStatus(String identifier);

    WsDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable);
}