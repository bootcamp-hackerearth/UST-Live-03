package com.ust.pos.shelf.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto delete(String identifier);

    PaginatedResponseDto<ShelfDto> findAll(Pageable pageable);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable);
}