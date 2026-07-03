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

    ShelfDto findById(Long id);

    WsDto<ShelfDto> findAll(Pageable pageable);

    WsDto<ShelfDto> findAll(Specification<Shelf> spec, Pageable pageable);

    void delete(String identifier);

    ShelfDto findByIdentifier(String identifier);

    ShelfDto changeShelfStatus(String identifier, boolean status);

    List<ShelfDto> findActiveShelf();

}