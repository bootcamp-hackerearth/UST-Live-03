package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.model.Brand;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {

    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    List<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<Brand> findActiveBrands();
}
