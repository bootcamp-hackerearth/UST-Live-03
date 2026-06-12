package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto save(BrandDto brandDto);

    void delete(String identifier);

    BrandDto findByIdentifier(String identifier);

    BrandDto update(BrandDto brandDto);

    BrandDto changeToggleStatus(String identifier, boolean status);

    List<BrandDto> findActiveStatus();
}
