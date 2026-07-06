package com.ust.pos.shelf.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    PaginationResponseDto<ShelfDto> findAll(Pageable pageable);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findByIdentifier(String identifier);

    void delete(String identifier);

    ShelfDto toggleStatus(String identifier,boolean status);

    List<ShelfDto> findActiveShelves();

    PaginationResponseDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable);
}