package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String username);

    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    BrandDto toggleStatus(String identifier, boolean status);

    List<BrandDto> findActiveBrands();
}