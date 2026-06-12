package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    ShelfDto findById(Long id);

    WsDto<ShelfDto> findAll(Pageable pageable);

    void deleteById(Long id);

    ShelfDto findByIdentifier(String identifier);

    ShelfDto changeShelfStatus(String identifier, boolean status);

    List<ShelfDto> findActiveShelf();

}