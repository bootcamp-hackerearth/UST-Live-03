package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface BrandService {

    BrandDto save(BrandDto brandDto);

    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    BrandDto update(BrandDto brandDto);

    BrandDto toggleStatus(String identifier, boolean status);

}