package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    List<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    BrandDto update(BrandDto brandDto);

    BrandDto toggleStatus(String identifier, boolean status);

    List<BrandDto> findActiveBrands();
}
