package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    void delete(String identifier);

    BrandDto update(BrandDto brandDto);

    List<BrandDto> findAll(Pageable pageable);

    List<BrandDto> findAll();

    BrandDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

}