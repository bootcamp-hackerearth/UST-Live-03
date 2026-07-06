package com.ust.pos.warehouse.service;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.WareHouse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface WareHouseService {

    WareHouseDto save(WareHouseDto wareHouseDto);
    WareHouseDto update(WareHouseDto wareHouseDto);
    boolean delete(String identifier);
    WsDto<WareHouseDto> findAll(Pageable pageable);
    WareHouseDto findByIdentifier(String identifier);
    List<WareHouseDto> findIfTrue();
    WareHouseDto toggleStatus(String identifier);
    WsDto<WareHouseDto> findAll(Specification<WareHouse> example, Pageable pageable, String keyword);

}