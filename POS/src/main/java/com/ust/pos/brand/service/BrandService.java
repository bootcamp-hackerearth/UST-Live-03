package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Brand;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    WsDto<BrandDto> findAll(Pageable pageable);

    BrandDto findByIdentifier(String brandCode);

    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    List<Brand> findActiveBrands();

    void toggleStatus(String identifier);
}
