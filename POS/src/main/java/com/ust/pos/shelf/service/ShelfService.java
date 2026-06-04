package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {

    ShelfDto createShelf(ShelfDto shelfDto);

    ShelfDto updateShelf(ShelfDto shelfDto);

    ShelfDto getShelf(Long id);

    List<ShelfDto> findAll(Pageable pageable);

    boolean deleteShelf(Long id);

    ShelfDto toggleStatus(Long id);

    List<ShelfDto> getActiveShelves();
}