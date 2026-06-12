package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    PaginationResponseDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    BrandDto update(BrandDto brandDto);

    BrandDto toggleStatus(String identifier, boolean status);
}