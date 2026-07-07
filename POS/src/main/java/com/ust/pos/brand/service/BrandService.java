package com.ust.pos.brand.service;

import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BrandService {
    BrandDto save(BrandDto brandDto);

    BrandDto update(BrandDto brandDto);

    void delete(String identifier);

    BrandDto findByIdentifier(String identifier);

    WsDto<BrandDto> findAll(Pageable pageable);

    List<BrandDto> findAllActive();

    BrandDto toggleStatus(String identifier);

    WsDto<BrandDto> findAll(Specification<Brand> example, Pageable pageable);
}