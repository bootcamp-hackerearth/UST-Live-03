package com.ust.pos.warehouse.service;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.model.Warehouse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.User;

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

    PageDto<WareHouseDto> findAll(Specification<Warehouse> spec, Pageable pageable, String keyword);
}
