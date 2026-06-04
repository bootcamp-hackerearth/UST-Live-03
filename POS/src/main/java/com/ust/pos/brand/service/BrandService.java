package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {

    BrandDto findByIdentifier(String identifier);

    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    List<BrandDto> findAll(Pageable pageable);

    List<BrandDto> findIfTrue();

    BrandDto toggleStatus(String identifier);
}

