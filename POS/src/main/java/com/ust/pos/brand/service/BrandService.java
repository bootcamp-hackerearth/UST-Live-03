package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void deleteByIdentifier(String identifier);

    BrandDto findByIdentifier(String identifier);

    List<BrandDto> findAll(Pageable pageable);
}
