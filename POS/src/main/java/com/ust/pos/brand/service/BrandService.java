package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    PaginationResponseDto<BrandDto> findAll(Pageable pageable);

    List<BrandDto> findByStatusTrue();

    BrandDto findByIdentifier(String identifier);

    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    BrandDto updateStatus(String identifier, boolean status);

    void delete(String identifier);
}
