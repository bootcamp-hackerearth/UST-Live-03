package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    BrandDto findByIdentifier(String identifier);

    List<BrandDto> findAll(Pageable pageable);

    List<BrandDto> findAllActive();

    BrandDto update(BrandDto brandDto);

    BrandDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
