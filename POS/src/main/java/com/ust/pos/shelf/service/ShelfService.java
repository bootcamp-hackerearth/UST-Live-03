package com.ust.pos.shelf.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.ShelfDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    void delete(String identifier);

    PaginatedResponseDto<ShelfDto> findAll(Pageable pageable);

    ShelfDto findByIdentifier(String identifier);

    List<ShelfDto> findAllActive();

    void changeStatus(String identifier, boolean status);

}
