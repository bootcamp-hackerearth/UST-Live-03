package com.ust.pos.shelf.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfService {
    PaginationResponseDto<ShelfDto> findAll(Pageable pageable);

    PaginationResponseDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findActiveShelfs();

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
