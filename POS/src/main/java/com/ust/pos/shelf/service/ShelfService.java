package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfService {
    ShelfDto save(ShelfDto shelfDto);

    WsDto<ShelfDto> findAll(Pageable pageable);

    WsDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable);

    List<ShelfDto> findAllActive();

    ShelfDto findByIdentifier(String identifier);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
