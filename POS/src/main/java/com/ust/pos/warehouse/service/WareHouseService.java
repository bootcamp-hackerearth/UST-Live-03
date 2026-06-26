package com.ust.pos.warehouse.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.WareHouseDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Transactional
public interface WareHouseService {
    WareHouseDto save(WareHouseDto wareHouseDto);

    WareHouseDto update(WareHouseDto wareHouseDto);

    boolean delete(String identifier);

    PageDto<WareHouseDto> findAll(Pageable pageable);

    WareHouseDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<WareHouseDto>findActiveWarehouses();
}
