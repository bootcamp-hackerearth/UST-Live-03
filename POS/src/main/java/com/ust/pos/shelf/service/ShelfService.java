package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShelfService {

    ShelfDto createShelf(ShelfDto shelfDto);

    ShelfDto updateShelf(ShelfDto shelfDto);

    ShelfDto getShelf(Long id);

    boolean deleteShelf(Long id);

    ShelfDto toggleStatus(Long id);

    List<ShelfDto> getActiveShelves();

    WsDto<ShelfDto> findAll(Pageable pageable);

    WsDto<ShelfDto> findAll(Specification<Shelf> spec, Pageable pageable, String keyword);

}