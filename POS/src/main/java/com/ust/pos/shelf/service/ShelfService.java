package com.ust.pos.shelf.service;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelfService {

    ShelfDto findByIdentifier(String identifier);

    ShelfDto save(ShelfDto shelfDto);

    ShelfDto update(ShelfDto shelfDto);

    void delete(String identifier);

    WsDto<ShelfDto> findAll(Pageable pageable);

    List<ShelfDto> findAllActive();

    ShelfDto toggleStatus(String identifier);

}